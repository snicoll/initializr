package io.spring.initializr.support

import io.spring.initializr.ProjectMetadata

/**
 *
 * @author Stephane Nicoll
 */
class ProjectMetadataBuilder {

	private final ProjectMetadata projectMetadata = new ProjectMetadata()


	static ProjectMetadataBuilder withDefaults() {
		new ProjectMetadataBuilder().addDefaults()
	}

	ProjectMetadata get() {
		projectMetadata.validate()
		projectMetadata
	}

	ProjectMetadataBuilder addDependencyGroup(String name, String... ids) {
		ProjectMetadata.DependencyGroup group = new ProjectMetadata.DependencyGroup()
		group.name = name
		for (String id : ids) {
			ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
			dependency.id = id
			group.content.add(dependency)
		}
		projectMetadata.dependencies.add(group)
		this
	}

	ProjectMetadataBuilder addDefaults() {
		addDefaultTypes().addDefaultPackagings().addDefaultJavaVersions()
				.addDefaultLanguages().addDefaultBootVersions()
	}

	ProjectMetadataBuilder addDefaultTypes() {
		addType('pom.xml', false, '/pom.xml').addType('starter.zip', true, '/starter.zip')
				.addType('build.gradle', false, '/build.gradle').addType('gradle.zip', false, '/starter.zip')
	}

	ProjectMetadataBuilder addType(String id, boolean defaultValue, String action) {
		ProjectMetadata.Type type = new ProjectMetadata.Type();
		type.id = id
		type.name = id
		type.default = defaultValue
		type.action = action
		projectMetadata.types.add(type)
		this
	}

	ProjectMetadataBuilder addDefaultPackagings() {
		addPackaging('jar', true).addPackaging('war', false)
	}

	ProjectMetadataBuilder addPackaging(String id, boolean defaultValue) {
		ProjectMetadata.Packaging packaging = new ProjectMetadata.Packaging();
		packaging.id = id
		packaging.name = id
		packaging.default = defaultValue
		projectMetadata.packagings.add(packaging)
		this
	}

	ProjectMetadataBuilder addDefaultJavaVersions() {
		addJavaVersion('1.6', false).addJavaVersion('1.7', true).addJavaVersion('1.8', false)
	}

	ProjectMetadataBuilder addJavaVersion(String version, boolean defaultValue) {
		ProjectMetadata.JavaVersion javaVersion = new ProjectMetadata.JavaVersion();
		javaVersion.id = version
		javaVersion.name = version
		javaVersion.default = defaultValue
		projectMetadata.javaVersions.add(javaVersion)
		this
	}

	ProjectMetadataBuilder addDefaultLanguages() {
		addLanguage('java', true).addPackaging('groovy', false)
	}

	ProjectMetadataBuilder addLanguage(String id, boolean defaultValue) {
		ProjectMetadata.Language language = new ProjectMetadata.Language();
		language.id = id
		language.name = id
		language.default = defaultValue
		projectMetadata.languages.add(language)
		this
	}

	ProjectMetadataBuilder addDefaultBootVersions() {
		addBootVersion('1.0.2.RELEASE', false).addBootVersion('1.1.5.RELEASE', true)
				.addBootVersion('1.2.0.BUILD-SNAPSHOT', false)
	}

	ProjectMetadataBuilder addBootVersion(String id, boolean defaultValue) {
		ProjectMetadata.BootVersion bootVersion = new ProjectMetadata.BootVersion();
		bootVersion.id = id
		bootVersion.name = id
		bootVersion.default = defaultValue
		projectMetadata.bootVersions.add(bootVersion)
		this
	}


}