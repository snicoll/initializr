/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.project;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MutableProjectDescription}.
 *
 * @author Stephane Nicoll
 */
class MutableProjectDescriptionTests {

	@Test
	void sealPlatformVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		Version original = Version.parse("1.0.0.RELEASE");
		description.setPlatformVersion(original);
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getPlatformVersion()).isEqualTo(original);
		sealed.setPlatformVersion(Version.parse("2.0.0.RELEASE"));
		assertThat(sealed.getOriginalDescription().getPlatformVersion()).isEqualTo(original);
	}

	@Test
	void sealBuildSystem() {
		MutableProjectDescription description = new MutableProjectDescription();
		BuildSystem original = BuildSystem.forId(MavenBuildSystem.ID);
		description.setBuildSystem(original);
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getBuildSystem()).isEqualTo(original);
		sealed.setBuildSystem(BuildSystem.forId(GradleBuildSystem.ID));
		assertThat(sealed.getOriginalDescription().getBuildSystem()).isEqualTo(original);
	}

	@Test
	void sealPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		Packaging original = Packaging.forId(JarPackaging.ID);
		description.setPackaging(original);
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getPackaging()).isEqualTo(original);
		sealed.setPackaging(Packaging.forId(WarPackaging.ID));
		assertThat(sealed.getOriginalDescription().getPackaging()).isEqualTo(original);
	}

	@Test
	void sealLanguage() {
		MutableProjectDescription description = new MutableProjectDescription();
		Language original = Language.forId(JavaLanguage.ID, "1.8");
		description.setLanguage(original);
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getLanguage()).isEqualTo(original);
		sealed.setLanguage(Language.forId(JavaLanguage.ID, "11"));
		assertThat(sealed.getOriginalDescription().getLanguage()).isEqualTo(original);
	}

	@Test
	void sealDependencies() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("one", mock(Dependency.class));
		description.addDependency("two", mock(Dependency.class));
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getRequestedDependencies()).containsOnlyKeys("one", "two");
		sealed.addDependency("three", mock(Dependency.class));
		assertThat(sealed.getOriginalDescription().getRequestedDependencies()).containsOnlyKeys("one", "two");
	}

	@Test
	void sealGroupId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("com.test");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getGroupId()).isEqualTo("com.test");
		sealed.setGroupId("com.another");
		assertThat(sealed.getOriginalDescription().getGroupId()).isEqualTo("com.test");
	}

	@Test
	void sealArtifactId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("test-demo");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getArtifactId()).isEqualTo("test-demo");
		sealed.setArtifactId("test-another");
		assertThat(sealed.getOriginalDescription().getArtifactId()).isEqualTo("test-demo");
	}

	@Test
	void sealVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setVersion("1.0.0-SNAPSHOT");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getVersion()).isEqualTo("1.0.0-SNAPSHOT");
		sealed.setVersion("2.0.0-SNAPSHOT");
		assertThat(sealed.getOriginalDescription().getVersion()).isEqualTo("1.0.0-SNAPSHOT");
	}

	@Test
	void sealName() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName("Test");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getName()).isEqualTo("Test");
		sealed.setName("Another");
		assertThat(sealed.getOriginalDescription().getName()).isEqualTo("Test");
	}

	@Test
	void sealDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setDescription("Test Project");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getDescription()).isEqualTo("Test Project");
		sealed.setDescription("Another Project");
		assertThat(sealed.getOriginalDescription().getDescription()).isEqualTo("Test Project");
	}

	@Test
	void sealApplicationName() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setApplicationName("TestApplication");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getApplicationName()).isEqualTo("TestApplication");
		sealed.setApplicationName("AnotherApplication");
		assertThat(sealed.getOriginalDescription().getApplicationName()).isEqualTo("TestApplication");
	}

	@Test
	void sealPackageName() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackageName("com.test.app");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getPackageName()).isEqualTo("com.test.app");
		sealed.setPackageName("com.another.app");
		assertThat(sealed.getOriginalDescription().getPackageName()).isEqualTo("com.test.app");
	}

	@Test
	void sealBaseDirectory() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBaseDirectory("test");
		MutableProjectDescription sealed = description.seal();
		assertThat(sealed.getOriginalDescription().getBaseDirectory()).isEqualTo("test");
		sealed.setBaseDirectory("another");
		assertThat(sealed.getOriginalDescription().getBaseDirectory()).isEqualTo("test");
	}

	@Test
	void nonSealedDescriptionHasOriginalEqualsToItself() {
		MutableProjectDescription description = new MutableProjectDescription();
		assertThat(description.getOriginalDescription()).isSameAs(description);
	}


}
