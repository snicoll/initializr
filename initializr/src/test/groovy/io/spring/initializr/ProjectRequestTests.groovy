package io.spring.initializr

import io.spring.initializr.support.InitializrMetadataBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 *
 * @author Stephane Nicoll
 */
class ProjectRequestTests {

	@Test
	public void resolve() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').get()

		request.style.add('web')
		request.style.add('spring-data')
		request.resolve(metadata)
		assertBootStarter(request.dependencies.get(0), 'web')
		assertBootStarter(request.dependencies.get(1), 'spring-data')
	}

	@Test
	public void resolveUnknownStyle() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar:1.0').get()

		request.style.add('org.foo:bar:1.0')
		request.style.add('foo-bar')
		request.resolve(metadata)
		assertDependency(request.dependencies.get(0), 'org.foo', 'bar', '1.0')
		assertBootStarter(request.dependencies.get(1), 'foo-bar')
	}

	private static void assertBootStarter(InitializrMetadata.Dependency actual, String name) {
		InitializrMetadata.Dependency expected = new InitializrMetadata.Dependency()
		expected.asSpringBootStarter(name)
		assertDependency(actual, expected.groupId, expected.artifactId, expected.version)
	}

	private static void assertDependency(InitializrMetadata.Dependency actual, String groupId,
										 String artifactId, String version) {
		assertEquals groupId, actual.groupId
		assertEquals artifactId, actual.artifactId
		assertEquals version, actual.version
	}
}
