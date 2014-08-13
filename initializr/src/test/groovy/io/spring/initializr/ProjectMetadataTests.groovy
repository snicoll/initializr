package io.spring.initializr

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
class ProjectMetadataTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	private final ProjectMetadata projectMetadata = new ProjectMetadata()

	@Test
	public void setCoordinatesFromId() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'org.foo:bar:1.2.3'
		projectMetadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertEquals '1.2.3', dependency.version
		assertEquals 'org.foo:bar:1.2.3', dependency.id
	}

	@Test
	public void setCoordinatesFromIdNoVersion() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'org.foo:bar'
		projectMetadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertNull dependency.version
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	public void setIdFromCoordinates() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		dependency.version = '1.0'
		projectMetadata.validateDependency(dependency)
		assertEquals 'org.foo:bar:1.0', dependency.id
	}

	@Test
	public void setIdFromCoordinatesNoVersion() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		projectMetadata.validateDependency(dependency)
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	public void setIdFromSimpleName() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'web'

		projectMetadata.validateDependency(dependency)
		assertEquals 'org.springframework.boot', dependency.groupId
		assertEquals 'spring-boot-starter-web', dependency.artifactId
		assertNull dependency.version
		assertEquals 'web', dependency.id
	}

	@Test
	public void invalidDependency() {
		thrown.expect(InvalidProjectMetadataException.class)
		projectMetadata.validateDependency(new ProjectMetadata.Dependency())
	}

	@Test
	public void invalidIdFormatTooManyColons() {
		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'org.foo:bar:1.0:test:external'

		thrown.expect(InvalidProjectMetadataException.class)
		projectMetadata.validateDependency(dependency)
	}

	@Test
	public void indexedDependencies() {
		ProjectMetadata metadata = new ProjectMetadata()
		ProjectMetadata.DependencyGroup group = new ProjectMetadata.DependencyGroup()

		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'first'
		group.content.add(dependency)
		ProjectMetadata.Dependency dependency2 = new ProjectMetadata.Dependency()
		dependency2.id = 'second'
		group.content.add(dependency2)

		metadata.dependencies.add(group)

		metadata.validate()

		assertSame dependency, metadata.getDependency('first')
		assertSame dependency2, metadata.getDependency('second')
		assertNull metadata.getDependency('anotherId')
	}

	@Test
	public void addTwoDependenciesWithSameId() {
		ProjectMetadata metadata = new ProjectMetadata()
		ProjectMetadata.DependencyGroup group = new ProjectMetadata.DependencyGroup()

		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'conflict'
		group.content.add(dependency)
		ProjectMetadata.Dependency dependency2 = new ProjectMetadata.Dependency()
		dependency2.id = 'conflict'
		group.content.add(dependency2)

		metadata.dependencies.add(group)

		thrown.expect(IllegalArgumentException.class)
		thrown.expectMessage('conflict')
		metadata.validate()
	}

	@Test
	public void addDependencyWithAliases() {
		ProjectMetadata metadata = new ProjectMetadata()
		ProjectMetadata.DependencyGroup group = new ProjectMetadata.DependencyGroup()

		ProjectMetadata.Dependency dependency = new ProjectMetadata.Dependency()
		dependency.id = 'first'
		dependency.aliases.add('alias1')
		dependency.aliases.add('alias2')
		group.content.add(dependency)
		metadata.dependencies.add(group)

		metadata.validate()

		assertSame dependency, metadata.getDependency('first')
		assertSame dependency, metadata.getDependency('alias1')
		assertSame dependency, metadata.getDependency('alias2')
	}

}
