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

	private final Dependency springCloudStreamReactive;

	public SpringCloudStreamRequestPostProcessor() {
		this.springCloudStreamKafka = Dependency.withId("cloud-stream-binder-kafka",
				"org.springframework.cloud", "spring-cloud-stream-binder-kafka");
		this.springCloudStreamReactive = Dependency.withId("cloud-stream-reactive",
				"org.springframework.cloud", "spring-cloud-stream-reactive");
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		Dependency springCloudStreamRabbit = getDependency(request,
				"cloud-stream");
		Dependency reactiveSpringCloudStreamRabbit = getDependency(request,
				"reactive-cloud-stream");
		boolean hasKafka = hasDependencies(request, "kafka");
		if (springCloudStreamRabbit != null && hasKafka) {
			request.getResolvedDependencies().remove(springCloudStreamRabbit);
			request.getResolvedDependencies().add(this.springCloudStreamKafka);
		}
		if (reactiveSpringCloudStreamRabbit != null) {
			request.getResolvedDependencies().add(this.springCloudStreamReactive);
			if (hasKafka) {
				request.getResolvedDependencies().remove(reactiveSpringCloudStreamRabbit);
				request.getResolvedDependencies().add(this.springCloudStreamKafka);
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

