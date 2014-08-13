package io.spring.initializr.web.support

import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.html.DomElement
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import com.gargoylesoftware.htmlunit.html.HtmlTextInput
import io.spring.initializr.support.ProjectAssert
import io.spring.initializr.support.ZipUtils

/**
 * Represent the home page of the service.
 *
 * @author Stephane Nicoll
 */
class HomePage {

	private final HtmlPage page

	HomePage(HtmlPage page) {
		this.page = page
	}

	String groupId
	String artifactId
	String name
	String description
	String packageName
	String type
	String packaging
	List<String> dependencies = []

	/**
	 * Generate a project using the specified temporary directory. Return
	 * the {@link ProjectAssert } instance.
	 * @see org.junit.rules.TemporaryFolder
	 */
	ProjectAssert generateProject(File outputDir) {
		setup()
		HtmlButton submit = page.getElementByName('generate-project')
		extractProject(submit, outputDir)
		return new ProjectAssert(outputDir)
	}

	/**
	 * Setup the {@link HtmlPage} with the customization of this
	 * instance. Only applied when a non-null value is set
	 */
	private void setup() {
		setTextValue('groupId', groupId)
		setTextValue('artifactId', artifactId)
		setTextValue('name', name)
		setTextValue('description', description)
		setTextValue('packageName', packageName)
		select('type', type)
		select('packaging', packaging)
		selectDependencies(dependencies)
	}

	private void setTextValue(String elementId, String value) {
		if (value != null) {
			HtmlTextInput input = page.getHtmlElementById(elementId)
			input.setValueAttribute(value)
		}
	}

	private void select(String selectId, String value) {
		if (value != null) {
			HtmlSelect input = page.getHtmlElementById(selectId)
			input.setSelectedAttribute(value, true)
		}
	}

	private void selectDependencies(List<String> dependencies) {
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

	static void extractProject(HtmlButton submit, File outputDir) {
		Page newMessagePage = submit.click();
		InputStream input = newMessagePage.getWebResponse().getContentAsStream()
		try {
			ZipUtils.unzip(input, outputDir)
		} finally {
			input.close()
		}
	}
}
