package io.spring.initializr.web

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.spring.initializr.support.ProjectAssert
import io.spring.initializr.web.support.HomePage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerFormIntegrationTests extends AbstractMainControllerIntegrationTests {

	@Autowired
	private WebApplicationContext context

	private WebClient webClient

	@Before
	public void setup() {
		MockMvc mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build()
		webClient = new WebClient()
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc, ''))
	}

	@After
	public void cleanup() {
		this.webClient.closeAllWindows()
	}

	@Test
	public void createDefaultProject() {
		HomePage page = home()
		ProjectAssert projectAssert = page.generateProject(folder.newFolder())
		projectAssert.isMavenProject().isJavaProject().hasStaticAndTemplatesResources(false)
				.pomAssert().hasDependenciesCount(1).hasSpringBootStarterDependency('test')
	}

	@Test
	public void createProjectWithCustomDefaults() {
		HomePage page = home()
		page.groupId = 'com.acme'
		page.artifactId = 'foo-bar'
		page.name = 'My project'
		page.description = 'A description for my project'
		page.dependencies << 'web' << 'data-jpa'
		ProjectAssert projectAssert = page.generateProject(folder.newFolder())
		projectAssert.isMavenProject().isJavaProject().hasStaticAndTemplatesResources(true)

		projectAssert.pomAssert().hasGroupId('com.acme').hasArtifactId('foo-bar')
				.hasName('My project').hasDescription('A description for my project')
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('test')
	}

	@Test
	public void createSimpleGradleProject() {
		HomePage page = home()
		page.type = 'gradle.zip'
		page.dependencies << 'data-jpa'
		ProjectAssert projectAssert = page.generateProject(folder.newFolder())
		projectAssert.isGradleProject().isJavaProject().hasStaticAndTemplatesResources(false)
	}

	@Test
	public void createWarProject() {
		HomePage page = home()
		page.packaging = 'war'
		ProjectAssert projectAssert = page.generateProject(folder.newFolder())
		projectAssert.isMavenProject().isJavaWebProject()
				.pomAssert().hasPackaging('war').hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web') // Added with war packaging
				.hasSpringBootStarterDependency('tomcat')
				.hasSpringBootStarterDependency('test')
	}


	HomePage home() {
		WebRequest request = new WebRequest(new URL('http://localhost/'), 'text/html')
		HtmlPage home = webClient.getPage(request)
		return new HomePage(home)
	}


}
