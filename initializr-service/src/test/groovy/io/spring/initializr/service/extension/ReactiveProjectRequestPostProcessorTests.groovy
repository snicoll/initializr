package io.spring.initializr.service.extension

import io.spring.initializr.generator.ProjectGenerator
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.metadata.DefaultMetadataElement
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.metadata.InitializrProperties
import io.spring.initializr.service.InitializrService
import io.spring.initializr.test.generator.PomAssert
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Tests for {@link ReactiveProjectRequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = InitializrService)
class ReactiveProjectRequestPostProcessorTests {

	@Autowired
	private ProjectGenerator projectGenerator

	@Autowired
	private InitializrMetadataProvider metadataProvider

	@Test
	void java8IsMandatory() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '1.4.0.BUILD-SNAPSHOT'
		request.javaVersion = '1.7'
		generateMavenPom(request).hasJavaVersion('1.8')
	}

	@Test
	void versionsAreOverridden() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '1.4.0.BUILD-SNAPSHOT'
		generateMavenPom(request)
				.hasProperty('spring.version', '5.0.0.BUILD-SNAPSHOT')
				.hasProperty('reactor.version', '3.0.0.BUILD-SNAPSHOT')
	}

	@Test
	void snapshotRepoIsAddedIfNecessary() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '1.4.1.RELEASE'
		generateMavenPom(request).hasRepository('spring-snapshots', 'Spring Snapshots',
				'https://repo.spring.io/snapshot', true)
	}

	@Test
	void simpleProjectUnaffected() {
		ProjectRequest request = createProjectRequest('web')
		request.javaVersion = '1.7'
		request.buildProperties.versions['spring.version'] = { '3.2.7.RELEASE' }
		generateMavenPom(request).hasJavaVersion('1.7')
				.hasProperty('spring.version', '3.2.7.RELEASE')
				.hasNoProperty('reactor.version')
	}


	private ProjectRequest createProjectRequest(String... styles) {
		def request = new ProjectRequest()
		request.initialize(metadataProvider.get())
		request.style.addAll Arrays.asList(styles)
		request
	}


	private PomAssert generateMavenPom(ProjectRequest request) {
		request.type = 'maven-build'
		def content = new String(projectGenerator.generateMavenPom(request))
		new PomAssert(content)
	}

	@Configuration
	static class Config {

		@Bean
		InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties) {
			def metadata = InitializrMetadataBuilder.fromInitializrProperties(properties).build()
			new DefaultInitializrMetadataProvider(metadata) {
				@Override
				protected List<DefaultMetadataElement> fetchBootVersions() {
					null // Disable metadata fetching from spring.io
				}
			}
		}

	}

}
