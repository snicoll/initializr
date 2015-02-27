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

/**
 *
 * @author Stephane Nicoll
 */
class DependenciesCapability extends ServiceCapability<List<DependencyGroup>>{

	DependenciesCapability() {
		super('dependencies', TYPE_HIERARCHICAL_MULTI_SELECT)
	}

	/*
		This is a dedicated object now so we could offer methods to order groups
		and/or dependencies, exclude dependencies by id, etc. That way the callback
		in the override would only have to "play" with those methods to build the
		expected structure.

		Note that excluding a value by id could be added in some sort of base class
	 	that should restrict the generic to List<? extends IdentifiableElement>

	 */
}
