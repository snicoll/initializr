package io.spring.initializr

import javax.annotation.PostConstruct

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.ToString

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Stephane Nicoll
 */
@ConfigurationProperties(prefix = 'projects', ignoreUnknownFields = false)
class ProjectMetadata {

	final List<DependencyGroup> dependencies = new ArrayList<DependencyGroup>()

	final List<Type> types = new ArrayList<Type>()

	final List<Packaging> packagings = new ArrayList<Packaging>()

	final List<JavaVersion> javaVersions = new ArrayList<JavaVersion>()

	final List<Language> languages = new ArrayList<Language>()

	final List<BootVersion> bootVersions = new ArrayList<BootVersion>()

	@JsonIgnore
	final Map<String, Dependency> indexedDependencies = new HashMap<String, Dependency>()

	/**
	 * Return the {@link Dependency} with the specified id or {@code null} if
	 * no such dependency exists.
	 */
	Dependency getDependency(String id) {
		return indexedDependencies.get(id)
	}

	/**
	 * Initialize and validate the configuration.
	 */
	@PostConstruct
	public void validate() {
		for (DependencyGroup group : dependencies) {
			for (Dependency dependency : group.getContent()) {
				validateDependency(dependency)

				Dependency existing = indexedDependencies.get(dependency.id)
				if (existing != null) {
					throw new IllegalArgumentException('Could not register ' + dependency +
							': another dependency has also the "'+dependency.id+  '" id ' + existing)
				}
				indexedDependencies.put(dependency.id, dependency)
			}
		}
	}

	static void validateDependency(Dependency dependency) {
		String id = dependency.getId()
		if (id == null) {
			if (!dependency.hasCoordinates()) {
				throw new InvalidProjectMetadataException('Invalid dependency, ' +
						'should have at least an id or a groupId/artifactId pair.')
			}
			StringBuilder sb = new StringBuilder()
			sb.append(dependency.getGroupId()).append(':').append(dependency.getArtifactId())
			if (dependency.getVersion() != null) {
				sb.append(':').append(dependency.getVersion())
			}
			dependency.setId(sb.toString())
		} else if (!dependency.hasCoordinates()) {
			// Let's build the coordinates from the id
			StringTokenizer st = new StringTokenizer(id, ':')
			if (st.countTokens() == 1) { // assume spring-boot-starter
				dependency.groupId = 'org.springframework.boot'
				dependency.artifactId = 'spring-boot-starter-' + id
			} else if (st.countTokens() == 2 || st.countTokens() == 3) {
				dependency.setGroupId(st.nextToken())
				dependency.setArtifactId(st.nextToken())
				if (st.hasMoreTokens()) {
					dependency.setVersion(st.nextToken())
				}
			} else {
				throw new InvalidProjectMetadataException('Invalid dependency, id should ' +
						'have the form groupId:artifactId[:version] but got ' + id)
			}
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class DependencyGroup {

		String name

		String description

		final List<Dependency> content = new ArrayList<Dependency>()

	}

	@ToString(ignoreNulls = true, includePackage = false)
	static class Dependency extends IdentifiableElement {

		@JsonIgnore
		def String groupId

		@JsonIgnore
		String artifactId

		@JsonIgnore
		String version

		/**
		 * Specify if the dependency has its coordinates set, i.e. {@code groupId}
		 * and {@code artifactId}.
		 */
		boolean hasCoordinates() {
			return groupId != null && artifactId != null
		}
	}

	static class Type extends DefaultIdentifiableElement {

		String action
	}

	static class Packaging extends DefaultIdentifiableElement {
	}

	static class JavaVersion extends DefaultIdentifiableElement {
	}

	static class Language extends DefaultIdentifiableElement {
	}

	static class BootVersion extends DefaultIdentifiableElement {
	}


	static class DefaultIdentifiableElement extends IdentifiableElement {

		boolean defaultValue

		void setDefault(boolean defaultValue) {
			this.defaultValue = defaultValue
		}

		boolean isDefault() {
			return this.defaultValue
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class IdentifiableElement {

		String name

		String id

		String description

		String getName() {
			(name != null ? name : id)
		}
	}
}
