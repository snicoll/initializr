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
class InitializrMetadata {

	final DependenciesCapability dependencies = new DependenciesCapability()

	final TypeCapability types = new TypeCapability()

	// was bootVersion
	final SingleSelectCapability frameworkVersion = new SingleSelectCapability('frameworkVersion')

	final SingleSelectCapability packagings = new SingleSelectCapability('packaging')

	final SingleSelectCapability javaVersions = new SingleSelectCapability('javaVersion')

	final SingleSelectCapability languages = new SingleSelectCapability('language')

	final TextCapability groupId = new TextCapability('groupId')

	final TextCapability version = new TextCapability('version')

	final TextCapability name = new TextCapability('name')

	final TextCapability description = new TextCapability('description')

	final TextCapability packageName = new TextCapability('packageName')

	/**
	 * Merge the content of the specified {@link InitializrMetadata} on this
	 * instance.
	 */
	// Can be smarter with strategy call back or something. The default would
	// append value only. In the case of dependencies, existing group would
	// be merged with additional starters added at the end
	def merge(InitializrMetadata metadata) {

	}

}
