package io.spring.initializr.web

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.html.*
import io.spring.initializr.TestApp
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApp.class)
@WebAppConfiguration
@ActiveProfiles('test-default')
class MainControllerFormIntegrationTests {

	@Autowired
	private WebApplicationContext context

	private WebClient webClient

	@Before
	public void setup() {
		MockMvc mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build()
		webClient = new WebClient()
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc))
	}

	@After
	public void cleanup() {
		this.webClient.closeAllWindows()
	}

	@Test
	@Ignore
	public void createSampleProject() {
		// Load the Create Message Form
		WebRequest request = new WebRequest(new URL('http://localhost/'), 'text/html')
		HtmlPage home = webClient.getPage(request)

		// Submit the create message form
		HtmlForm form = home.getHtmlElementById('form')
		HtmlTextInput groupId = home.getHtmlElementById('groupId')
		groupId.setValueAttribute('com.acme')
		HtmlTextInput artifactId = home.getHtmlElementById('artifactId')
		artifactId.setValueAttribute('foo-bar')

		selectDependencies(home, 'web')
		HtmlButton submit = home.getElementByName('generate-project')
		HtmlPage newMessagePage = submit.click();   // npe here

	}

	def selectDependencies(HtmlPage page, String... dependencies) {
		List<DomElement> styles = page.getElementsByName("style")
		Map<String, HtmlCheckBoxInput> allStyles = new HashMap<>()
		for (HtmlCheckBoxInput checkBoxInput : styles) {
			allStyles.put(checkBoxInput.getValueAttribute(), checkBoxInput)
		}
		for (String dependency : dependencies) {
			HtmlCheckBoxInput checkBox = allStyles.get(dependency)
			if (checkBox != null) {
				checkBox.checked = true
			} else {
				throw new IllegalArgumentException('No dependency with name '
					+ dependency + ' was found amongst ' + allStyles.keySet());
			}
		}
	}

}
