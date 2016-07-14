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

/**
 *
 * @author Stephane Nicoll
 */
class SourceModel {

	// Maybe that should be an implementation detail and SourceModel should
	// be an interface?
	String templatePath

	final Set<String> imports = []

	/**
	 * Add an annotation at class level.
	 * @param fqn the annotation to add
	 * @param the annotation attributes (can be null)
	 */
	void addClassAnnotation(String fqn, Map<String, Object> attributes) {
		// register the annotation and the import(s)
	}


}
