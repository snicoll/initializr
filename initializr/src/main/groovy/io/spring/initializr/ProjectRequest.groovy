package io.spring.initializr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A request to generate a project. Each instance should be resolved against an
 * {@link InitializrMetadata} instance.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
class ProjectRequest {

	private static final Logger logger = LoggerFactory.getLogger(ProjectRequest.class)

	def style = []

	String name
	String type
	String description
	String groupId
	String artifactId
	String version
	String bootVersion
	String packaging
	String language
	String packageName
	String javaVersion

	def dependencies = []
	def facets = []

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}
	 */
	void resolve(InitializrMetadata metadata) {
		if (style == null || style.size() == 0) {
			style = []
		}
		if (!style.class.isArray() && !(style instanceof Collection)) {
			style = [style]
		}
		dependencies = style.collect {
			InitializrMetadata.Dependency dependency = metadata.getDependency(it);
			if (dependency == null) {
				logger.warn("No known dependency for style " + it + " assuming spring-boot-starter")
				dependency = new InitializrMetadata.Dependency()
				dependency.asSpringBootStarter(it)
			}
			dependency
		}
		dependencies.each {
			it.facets.each {
				if (!facets.contains(it)) {
					facets.add(it)
				}
			}
		}
		handleFacets(metadata)
	}

	protected handleFacets(InitializrMetadata metadata) {
		if (packaging == 'war' && !hasWebFacet()) {
			// Need to be able to bootstrap the app
			dependencies << metadata.getDependency('web')
			facets << 'web'
		}
	}

	/**
	 * Specify if this request has the web facet enabled.
	 */
	boolean hasWebFacet() {
		hasFacet('web')
	}

	/**
	 * Specify if this request has the specified facet enabled
	 */
	boolean hasFacet(String facet) {
		facets.contains(facet)
	}
}
