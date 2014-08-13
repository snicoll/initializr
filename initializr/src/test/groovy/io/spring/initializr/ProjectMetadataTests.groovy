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

	private final InitializrMetadata metadata = new InitializrMetadata()

	@Test
	public void setCoordinatesFromId() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'org.foo:bar:1.2.3'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertEquals '1.2.3', dependency.version
		assertEquals 'org.foo:bar:1.2.3', dependency.id
	}

	@Test
	public void setCoordinatesFromIdNoVersion() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'org.foo:bar'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertNull dependency.version
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	public void setIdFromCoordinates() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		dependency.version = '1.0'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo:bar:1.0', dependency.id
	}

	@Test
	public void setIdFromCoordinatesNoVersion() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	public void setIdFromSimpleName() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'web'

		metadata.validateDependency(dependency)
		assertEquals 'org.springframework.boot', dependency.groupId
		assertEquals 'spring-boot-starter-web', dependency.artifactId
		assertNull dependency.version
		assertEquals 'web', dependency.id
	}

	@Test
	public void invalidDependency() {
		thrown.expect(InvalidInitializrMetadataException.class)
		metadata.validateDependency(new InitializrMetadata.Dependency())
	}

	@Test
	public void invalidIdFormatTooManyColons() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'org.foo:bar:1.0:test:external'

		thrown.expect(InvalidInitializrMetadataException.class)
		metadata.validateDependency(dependency)
	}

	@Test
	public void indexedDependencies() {
		InitializrMetadata metadata = new InitializrMetadata()
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()

		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'first'
		group.content.add(dependency)
		InitializrMetadata.Dependency dependency2 = new InitializrMetadata.Dependency()
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
		InitializrMetadata metadata = new InitializrMetadata()
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()

		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'conflict'
		group.content.add(dependency)
		InitializrMetadata.Dependency dependency2 = new InitializrMetadata.Dependency()
		dependency2.id = 'conflict'
		group.content.add(dependency2)

		metadata.dependencies.add(group)

		thrown.expect(IllegalArgumentException.class)
		thrown.expectMessage('conflict')
		metadata.validate()
	}

	@Test
	public void addDependencyWithAliases() {
		InitializrMetadata metadata = new InitializrMetadata()
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()

		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
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
