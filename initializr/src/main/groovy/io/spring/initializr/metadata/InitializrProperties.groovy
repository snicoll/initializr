/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import io.spring.initializr.InitializrConfiguration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration of the initializr service.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ConfigurationProperties(prefix = 'initializr', ignoreUnknownFields = false)
class InitializrProperties extends InitializrConfiguration {

	@JsonIgnore
	final List<DependencyGroup> dependencies = []

	@JsonIgnore
	final List<Type> types = []

	@JsonIgnore
	final List<DefaultMetadataElement> packagings = []

	@JsonIgnore
	final List<DefaultMetadataElement> javaVersions = []

	@JsonIgnore
	final List<DefaultMetadataElement> languages = []

	@JsonIgnore
	final List<DefaultMetadataElement> bootVersions = []

	@JsonIgnore
	final Defaults defaults = new Defaults()

	static class Defaults {

		String groupId = 'org.test'
		String artifactId
		String version = '0.0.1-SNAPSHOT'
		String name = 'demo'
		String description = 'Demo project for Spring Boot'
		String packageName

	}

}
