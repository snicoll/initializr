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

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author Stephane Nicoll
 */
@ConfigurationProperties(prefix = 'initializr', ignoreUnknownFields = false)
class InitializrProperties {

	// We reuse the same types to avoid duplication between config and meta-data

	final List<DependencyGroup> dependencies = []

	final List<DefaultIdentifiableElement> types = []

	final List<DefaultIdentifiableElement> packagings = []

	final List<DefaultIdentifiableElement> javaVersions = []

	final List<DefaultIdentifiableElement> languages = []

	final List<DefaultIdentifiableElement> frameworkVersions = []

	final Defaults defaults = new Defaults()

	final Env env = new Env()

	static class Defaults {

		static final String DEFAULT_NAME = 'demo'

		String groupId = 'org.test'
		String artifactId
		String version = '0.0.1-SNAPSHOT'
		String name = DEFAULT_NAME
		String description = 'Demo project for Spring Boot'
		String packageName
		String type
		String packaging
		String javaVersion
		String language
		String bootVersion

		/**
		 * Return the artifactId or the name of the project if none is set.
		 */
		String getArtifactId() {
			artifactId == null ? name : artifactId
		}

		/**
		 * Return the package name or the name of the project if none is set
		 */
		String getPackageName() {
			packageName == null ? name.replace('-', '.') : packageName
		}

	}

	/**
	 * Defines additional environment settings
	 */
	static class Env {

		String artifactRepository = 'https://repo.spring.io/release/'

		String springBootMetadataUrl = 'https://spring.io/project_metadata/spring-boot'

		/**
		 * The application name to use if none could be generated.
		 */
		String fallbackApplicationName = 'Application'

		/**
		 * The list of invalid application names. If such name is chosen or generated,
		 * the {@link #fallbackApplicationName} should be used instead.
		 */
		List<String> invalidApplicationNames = [
				'SpringApplication',
				'SpringBootApplication'
		]

		boolean forceSsl = true

		void validate() {
			if (!artifactRepository.endsWith('/')) {
				artifactRepository = artifactRepository + '/'
			}
		}

	}
}
