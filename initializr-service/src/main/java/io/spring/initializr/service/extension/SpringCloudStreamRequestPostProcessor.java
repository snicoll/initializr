/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.stereotype.Component;

/**
 *
 * @author Stephane Nicoll
 */
@Component
public class SpringCloudStreamRequestPostProcessor
		implements ProjectRequestPostProcessor {

	private final Dependency springCloudStreamKafka;

	private final Dependency springCloudStreamRabbit;

	private final Dependency streamTest;

	public SpringCloudStreamRequestPostProcessor() {
		this.springCloudStreamKafka = Dependency.withId("cloud-stream-binder-kafka",
				"org.springframework.cloud", "spring-cloud-stream-binder-kafka");
		this.springCloudStreamRabbit = Dependency.withId("cloud-stream-binder-rabbit",
				"org.springframework.cloud", "spring-cloud-stream-binder-rabbit");
		this.streamTest = Dependency.withId("stream-test", "org.springframework.cloud",
				"spring-cloud-stream-test-support");
		this.streamTest.setScope(Dependency.SCOPE_TEST);
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		Dependency springCloudStream = getDependency(request, "cloud-stream");
		boolean hasOtherStream = hasDependencies(request, "reactive-cloud-stream",
				"cloud-turbine-stream", "cloud-bus");
		boolean hasReactiveStream = hasDependencies(request, "reactive-cloud-stream");
		boolean hasKafka = hasDependencies(request, "kafka");
		boolean hasRabbit = hasDependencies(request, "rabbit");
		if (springCloudStream != null) {
			if (hasKafka) {
				request.getResolvedDependencies().remove(springCloudStream);
				request.getResolvedDependencies().add(this.springCloudStreamKafka);
			}
			if (hasRabbit) {
				request.getResolvedDependencies().remove(springCloudStream);
				request.getResolvedDependencies().add(this.springCloudStreamRabbit);
			}
		}
		if (hasReactiveStream || springCloudStream != null) {
			request.getResolvedDependencies().add(this.streamTest);
		}
		if (hasOtherStream) {
			if (hasKafka) {
				request.getResolvedDependencies().add(this.springCloudStreamKafka);
			}
			if (hasRabbit) {
				request.getResolvedDependencies().add(this.springCloudStreamRabbit);
			}
		}

	}

	// TODO: share this

	private boolean hasDependencies(ProjectRequest request, String... dependenciesId) {
		for (String id : dependenciesId) {
			if (getDependency(request, id) == null) {
				return false;
			}
		}
		return true;
	}

	private Dependency getDependency(ProjectRequest request, String id) {
		return request.getResolvedDependencies().stream()
				.filter(d -> id.equals(d.getId())).findFirst().orElse(null);
	}

}
