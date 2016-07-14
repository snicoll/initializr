/*
 * Copyright 2012-2016 the original author or authors.
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

package io.initializr.model

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.Repository

/**
 *
 * @author Stephane Nicoll
 */
class BuildModel {

	String groupId
	String artifactId
	String version
	String packaging
	String name
	String description

	final Map<String, Dependency> dependencies = [:]
	final Map<String, Repository> repositories = [:]
	final Map<String, BillOfMaterials> boms = [:]
	final TreeMap<String, Closure<String>> buildProperties = new TreeMap<>()
	final TreeMap<String, Closure<String>> versionProperties = new TreeMap<>()

}
