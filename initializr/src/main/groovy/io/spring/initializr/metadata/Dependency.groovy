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

import groovy.transform.ToString
import io.spring.initializr.InvalidInitializrMetadataException

/**
 *
 * @author Stephane Nicoll
 */
@ToString(ignoreNulls = true, includePackage = false)
class Dependency extends IdentifiableElement {

	static final String SCOPE_COMPILE = 'compile'
	static final String SCOPE_RUNTIME = 'runtime'
	static final String SCOPE_PROVIDED = 'provided'
	static final String SCOPE_TEST = 'test'
	static final List<String> SCOPE_ALL = [
			SCOPE_COMPILE,
			SCOPE_RUNTIME,
			SCOPE_PROVIDED,
			SCOPE_TEST
	]

	List<String> aliases = []

	List<String> facets = []

	String groupId

	String artifactId

	String version

	String scope = SCOPE_COMPILE

	String description

	String versionRange

	void setScope(String scope) {
		if (!SCOPE_ALL.contains(scope)) {
			throw new InvalidInitializrMetadataException("Invalid scope $scope must be one of $SCOPE_ALL")
		}
		this.scope = scope
	}

	void setVersionRange(String versionRange) {
		this.versionRange = versionRange ? versionRange.trim() : null
	}

	/**
	 * Specify if the dependency has its coordinates set, i.e. {@code groupId}
	 * and {@code artifactId}.
	 */
	boolean hasCoordinates() {
		groupId && artifactId
	}

	/**
	 * Define this dependency as a standard spring boot starter with the specified name
	 * <p>If no name is specified, the root 'spring-boot-starter' is assumed.
	 */
	Dependency asSpringBootStarter(String name) {
		groupId = 'org.springframework.boot'
		artifactId = name ? 'spring-boot-starter-' + name : 'spring-boot-starter'
		if (name) {
			id = name
		}
		this
	}

	/**
	 * Generate an id using the groupId and artifactId
	 */
	def generateId() {
		if (groupId == null || artifactId == null) {
			throw new IllegalArgumentException(
					"Could not generate id for $this: at least groupId and artifactId must be set.")
		}
		StringBuilder sb = new StringBuilder()
		sb.append(groupId).append(':').append(artifactId)
		id = sb.toString()
	}
}
