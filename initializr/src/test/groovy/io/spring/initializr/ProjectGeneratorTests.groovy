package io.spring.initializr

import io.spring.initializr.support.InitializrMetadataBuilder
import io.spring.initializr.support.PomAssert
import org.junit.Before
import org.junit.Test

/**
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorTests {

	private final ProjectGenerator projectGenerator = new ProjectGenerator()

	@Before
	void setup() {
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration').get()
		projectGenerator.metadata = metadata
	}

	@Test
	public void defaultMavenPom() {
		ProjectRequest request = createProjectRequest('web')
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasNoRepository().hasSpringBootStarterDependency('web')
	}

	@Test
	public void mavenPomWithBootSnapshot() {
		ProjectRequest request = createProjectRequest('web')
		request.bootVersion = '1.0.1.BUILD-SNAPSHOT'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSnapshotRepository().hasSpringBootStarterDependency('web')
	}

	@Test
	public void mavenPomWithWebFacet() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).get()
		projectGenerator.metadata = metadata

		ProjectRequest request = createProjectRequest('thymeleaf')
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasDependency('org.foo', 'thymeleaf')
				.hasDependenciesCount(2)

	}

	@Test
	public void mavenWarPomWithWebFacet() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).get()
		projectGenerator.metadata = metadata

		ProjectRequest request = createProjectRequest('thymeleaf')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSpringBootStarterDependency('tomcat')
				.hasDependency('org.foo', 'thymeleaf') // This is tagged as web facet so it brings the web one
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(3)

	}

	@Test
	public void mavenWarPomWithoutWebFacet() {
		ProjectRequest request = createProjectRequest('data-jpa')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSpringBootStarterDependency('tomcat')
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web') // Added by web facet
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(4)

	}

	PomAssert generateMavenPom(ProjectRequest request) {
		String content = new String(projectGenerator.generateMavenPom(request))
		return new PomAssert(content).validateProjectRequest(request)
	}

	ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest()
		projectGenerator.metadata.initializeProjectRequest(request)
		request.style.addAll Arrays.asList(styles)
		request
	}

}
