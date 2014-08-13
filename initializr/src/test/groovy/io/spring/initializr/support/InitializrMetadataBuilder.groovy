package io.spring.initializr.support

import io.spring.initializr.InitializrMetadata

/**
 *
 * @author Stephane Nicoll
 */
class InitializrMetadataBuilder {

	private final InitializrMetadata metadata = new InitializrMetadata()


	static InitializrMetadataBuilder withDefaults() {
		new InitializrMetadataBuilder().addDefaults()
	}

	InitializrMetadata get() {
		metadata.validate()
		metadata
	}

	InitializrMetadataBuilder addDependencyGroup(String name, String... ids) {
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()
		group.name = name
		for (String id : ids) {
			InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
			dependency.id = id
			group.content.add(dependency)
		}
		metadata.dependencies.add(group)
		this
	}

	InitializrMetadataBuilder addDefaults() {
		addDefaultTypes().addDefaultPackagings().addDefaultJavaVersions()
				.addDefaultLanguages().addDefaultBootVersions()
	}

	InitializrMetadataBuilder addDefaultTypes() {
		addType('pom.xml', false, '/pom.xml').addType('starter.zip', true, '/starter.zip')
				.addType('build.gradle', false, '/build.gradle').addType('gradle.zip', false, '/starter.zip')
	}

	InitializrMetadataBuilder addType(String id, boolean defaultValue, String action) {
		InitializrMetadata.Type type = new InitializrMetadata.Type();
		type.id = id
		type.name = id
		type.default = defaultValue
		type.action = action
		metadata.types.add(type)
		this
	}

	InitializrMetadataBuilder addDefaultPackagings() {
		addPackaging('jar', true).addPackaging('war', false)
	}

	InitializrMetadataBuilder addPackaging(String id, boolean defaultValue) {
		InitializrMetadata.Packaging packaging = new InitializrMetadata.Packaging();
		packaging.id = id
		packaging.name = id
		packaging.default = defaultValue
		metadata.packagings.add(packaging)
		this
	}

	InitializrMetadataBuilder addDefaultJavaVersions() {
		addJavaVersion('1.6', false).addJavaVersion('1.7', true).addJavaVersion('1.8', false)
	}

	InitializrMetadataBuilder addJavaVersion(String version, boolean defaultValue) {
		InitializrMetadata.JavaVersion javaVersion = new InitializrMetadata.JavaVersion();
		javaVersion.id = version
		javaVersion.name = version
		javaVersion.default = defaultValue
		metadata.javaVersions.add(javaVersion)
		this
	}

	InitializrMetadataBuilder addDefaultLanguages() {
		addLanguage('java', true).addPackaging('groovy', false)
	}

	InitializrMetadataBuilder addLanguage(String id, boolean defaultValue) {
		InitializrMetadata.Language language = new InitializrMetadata.Language();
		language.id = id
		language.name = id
		language.default = defaultValue
		metadata.languages.add(language)
		this
	}

	InitializrMetadataBuilder addDefaultBootVersions() {
		addBootVersion('1.0.2.RELEASE', false).addBootVersion('1.1.5.RELEASE', true)
				.addBootVersion('1.2.0.BUILD-SNAPSHOT', false)
	}

	InitializrMetadataBuilder addBootVersion(String id, boolean defaultValue) {
		InitializrMetadata.BootVersion bootVersion = new InitializrMetadata.BootVersion();
		bootVersion.id = id
		bootVersion.name = id
		bootVersion.default = defaultValue
		metadata.bootVersions.add(bootVersion)
		this
	}


}