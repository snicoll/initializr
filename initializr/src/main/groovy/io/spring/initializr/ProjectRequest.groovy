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

	String name = 'demo'
	String type = 'starter'
	String description = 'Demo project for Spring Boot'
	String groupId = 'org.test'
	String artifactId
	String version = '0.0.1-SNAPSHOT'
	String bootVersion
	String packaging = 'jar'
	String language = 'java'
	String packageName
	String javaVersion = '1.7'

	/**
	 * Return the artifactId or the name of the project if none is set.
	 */
	String getArtifactId() {
		artifactId == null ? name : artifactId
	}

	/**
	 * Return the package name or the name of the project if none is set
	 */
	String getPackageName() {
		packageName == null ? name.replace('-', '.') : packageName
	}

	/**
	 * Resolve this instance against the specified {@link ProjectMetadata}
	 */
	List<ProjectMetadata.Dependency> resolveDependencies(ProjectMetadata projectMetadata) {
		if (style == null || style.size() == 0) {
			style = []
		}
		if (!style.class.isArray() && !(style instanceof Collection)) {
			style = [style]
		}
		style.collect {
			ProjectMetadata.Dependency dependency = projectMetadata.getDependency(it);
			if (dependency == null) {
				logger.warn("No known dependency for style "+it+" assuming spring-boot-starter")
				dependency =  new ProjectMetadata.Dependency()
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
