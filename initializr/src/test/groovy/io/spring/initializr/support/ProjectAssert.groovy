package io.spring.initializr.support

import static org.junit.Assert.assertEquals

/**
 *
 * @author Stephane Nicoll
 */
class ProjectAssert {

	final File dir;

	ProjectAssert(File dir) {
		this.dir = dir
	}

	PomAssert pomAssert() {
		new PomAssert(file('pom.xml').text)
	}

	ProjectAssert isMavenProject() {
		hasFile('pom.xml').hasNoFile('build.gradle')
	}

	ProjectAssert isGradleProject() {
		hasFile('build.gradle').hasNoFile('pom.xml')
	}

	ProjectAssert isJavaProject() {
		hasFile('src/main/java/demo/Application.java',
				'src/test/java/demo/ApplicationTests.java',
				'src/main/resources/application.properties')
	}

	ProjectAssert isJavaWebProject() {
		isJavaProject().hasStaticAndTemplatesResources(true)
				.hasFile('src/main/java/demo/ServletInitializer.java')
	}

	ProjectAssert hasStaticAndTemplatesResources(boolean web) {
		assertFile('src/main/resources/templates', web)
		assertFile('src/main/resources/static', web)
	}

	ProjectAssert hasFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, true)
		}
		this
	}

	ProjectAssert hasNoFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, false)
		}
		this
	}

	ProjectAssert assertFile(String localPath, boolean exist) {
		def candidate = file(localPath)
		assertEquals 'Invalid presence (' + exist + ') for ' + localPath, exist, candidate.exists()
		this
	}

	private File file(String localPath) {
		new File(dir, localPath)
	}


}
