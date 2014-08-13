package io.spring.initializr

import io.spring.initializr.support.PomAssert
import io.spring.initializr.support.InitializrMetadataBuilder
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
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration') .get()
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

	PomAssert generateMavenPom(ProjectRequest request) {
		String content = new String(projectGenerator.generateMavenPom(request))
		return new PomAssert(content).validateProjectRequest(request)
	}

	ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = projectGenerator.metadata.createProjectRequest()
		request.style.addAll Arrays.asList(styles)
		request
	}

}
