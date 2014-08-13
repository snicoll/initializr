package io.spring.initializr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
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

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}
	 */
	List<InitializrMetadata.Dependency> resolveDependencies(InitializrMetadata projectMetadata) {
		if (style == null || style.size() == 0) {
			style = []
		}
		if (!style.class.isArray() && !(style instanceof Collection)) {
			style = [style]
		}
		style.collect {
			InitializrMetadata.Dependency dependency = projectMetadata.getDependency(it);
			if (dependency == null) {
				logger.warn("No known dependency for style "+it+" assuming spring-boot-starter")
				dependency =  new InitializrMetadata.Dependency()
				dependency.asSpringBootStarter(it)
			}
			dependency
		}
	}


	boolean isWebStyle() {
		style.any { webStyle(it) }
	}

	private boolean webStyle(String style) {
		style.contains('web') || style.contains('thymeleaf') || style.contains('freemarker') || style.contains('velocity') || style.contains('groovy-template')
	}
}
