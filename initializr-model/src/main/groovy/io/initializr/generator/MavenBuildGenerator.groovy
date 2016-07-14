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

package io.initializr.generator

import io.initializr.model.ProjectModel

/**
 *
 * @author Stephane Nicoll
 */
class MavenBuildGenerator implements BuildGenerator {

	@Override
	void generateBuild(ProjectModel model, File directory) {
		// Use model#metadata to fetch the parent pom to use. This is a Maven
		// specific feature that is hidden in the generator rather than
		// polluting the main model

		// initialize model, requires language + java version from main model
		// generate pom
		// generate wrapper classes (.mvn and friends)
	}

}
