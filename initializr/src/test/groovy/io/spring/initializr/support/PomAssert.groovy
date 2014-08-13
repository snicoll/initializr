package io.spring.initializr.support

import io.spring.initializr.ProjectRequest
import org.custommonkey.xmlunit.SimpleNamespaceContext
import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XpathEngine
import org.w3c.dom.Document

import static org.junit.Assert.assertEquals

/**
 * XPath assertions that are specific to a standard Maven POM.
 *
 * @author Stephane Nicoll
 */
class PomAssert {

	final XpathEngine eng
	final Document doc

	PomAssert(String content) {
		eng = XMLUnit.newXpathEngine()
		Map<String, String> context = new HashMap<String, String>()
		context.put 'pom', 'http://maven.apache.org/POM/4.0.0'
		SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(context)
		eng.namespaceContext = namespaceContext
		doc = XMLUnit.buildControlDocument(content);
	}

	/**
	 * Validate that this generated pom validates against its request.
	 */
	PomAssert validateProjectRequest(ProjectRequest request) {
		hasGroupId(request.groupId).hasArtifactId(request.artifactId).hasVersion(request.version).
				hasPackaging(request.packaging).hasName(request.name).hasDescription(request.description).
				hasBootVersion(request.bootVersion).hasJavaVersion(request.javaVersion)

	}

	PomAssert hasGroupId(String groupId) {
		assertEquals groupId, eng.evaluate(createRootNodeXPath('groupId'), doc);
		return this
	}

	PomAssert hasArtifactId(String artifactId) {
		assertEquals artifactId, eng.evaluate(createRootNodeXPath('artifactId'), doc);
		return this
	}

	PomAssert hasVersion(String version) {
		assertEquals version, eng.evaluate(createRootNodeXPath('version'), doc);
		return this
	}

	PomAssert hasPackaging(String packaging) {
		assertEquals packaging, eng.evaluate(createRootNodeXPath('packaging'), doc);
		return this
	}

	PomAssert hasName(String name) {
		assertEquals name, eng.evaluate(createRootNodeXPath('name'), doc);
		return this
	}

	PomAssert hasDescription(String description) {
		assertEquals description, eng.evaluate(createRootNodeXPath('description'), doc);
		return this
	}

	PomAssert hasBootVersion(String bootVersion) {
		assertEquals bootVersion, eng.evaluate(createRootNodeXPath('parent/pom:version'), doc)
		return this
	}

	PomAssert hasJavaVersion(String javaVersion) {
		assertEquals javaVersion, eng.evaluate(createPropertyNodeXpath('java.version'), doc)
		return this
	}

	PomAssert hasStartClass(String fqn) {
		assertEquals fqn, eng.evaluate(createPropertyNodeXpath('start-class'), doc)
		return this
	}

	PomAssert hasNoRepository() {
		assertEquals 0, eng.getMatchingNodes(createRootNodeXPath('repositories'), doc).length
		return this
	}

	PomAssert hasSnapshotRepository() {
		hasRepository('spring-snapshots')
		hasPluginRepository('spring-snapshots')
		return this
	}

	def hasRepository(String name) {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('repositories/pom:repository/pom:id'), doc)
		for (int i= 0; i < nodes.getLength(); i++) {
			if (name.equals(nodes.item(i).getTextContent())) {
				return;
			}
		}
		throw new IllegalArgumentException('No repository found with id ' +name)
	}
	def hasPluginRepository(String name) {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('pluginRepositories/pom:pluginRepository/pom:id'), doc)
		for (int i= 0; i < nodes.getLength(); i++) {
			if (name.equals(nodes.item(i).getTextContent())) {
				return;
			}
		}
		throw new IllegalArgumentException('No plugin repository found with id ' +name)
	}

	static String createPropertyNodeXpath(String propertyName) {
		createRootNodeXPath('properties/pom:' + propertyName)
	}

	static String createRootNodeXPath(String node) {
		'/pom:project/pom:' + node
	}
}
