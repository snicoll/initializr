package io.spring.initializr

import io.spring.initializr.support.PomAssert
import io.spring.initializr.support.ProjectMetadataBuilder
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
		InitializrMetadata metadata = ProjectMetadataBuilder.withDefaults()
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration') .get()
		projectGenerator.metadata = metadata
	}

	@Test
	public void defaultMavenPom() {
		ProjectRequest request = createProjectRequest('web')
		generateMavenPom(request).hasStartClass('demo.Application').hasNoRepository()
	}

	@Test
	public void mavenPomWithBootSnapshot() {
		ProjectRequest request = createProjectRequest('web')
		request.bootVersion = '1.0.1.BUILD-SNAPSHOT'
		generateMavenPom(request).hasStartClass('demo.Application').hasSnapshotRepository()
	}

	PomAssert generateMavenPom(ProjectRequest request) {
		String content = new String(projectGenerator.generateMavenPom(request))
		return new PomAssert(content).validateProjectRequest(request)
	}

	static ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest()
		request.bootVersion = '1.0.1.RELEASE'
		request.style.addAll Arrays.asList(styles)
		request
	}

}
