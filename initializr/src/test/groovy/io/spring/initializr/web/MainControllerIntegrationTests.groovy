package io.spring.initializr.web

import io.spring.initializr.TestApp
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApp.class)
@WebAppConfiguration
@ActiveProfiles('test-dummy')
@IntegrationTest('server.port=0')
class MainControllerIntegrationTests {

	@Value('${local.server.port}')
	private int port

	private final RestTemplate restTemplate = new RestTemplate()

	@Test
	public void validateProjectMetadata() {
		String json = restTemplate.getForObject(createUrl('/'), String.class)
		JsonAssert rootAssert = new JsonAssert(json)

		rootAssert.hasNoField('indexedDependencies')
		rootAssert.assertSize(6)

		rootAssert.assertRootSize('dependencies', 2)
		JsonAssert dependenciesAssert = rootAssert.getChild('dependencies')
		dependenciesAssert.assertArraySize(2)  // 2 groups


		JsonAssert coreGroupAssert = dependenciesAssert.getElement(0)
		coreGroupAssert.assertField('name', 'Core')
		coreGroupAssert.assertField('description', 'The core dependencies')
		coreGroupAssert.assertRootSize('content', 1)
		JsonAssert webDependency = coreGroupAssert.getChild('content').getElement(0)
		assertDependency(webDependency, 'web',
				'Web', 'Necessary infrastructure to build a REST service')


		JsonAssert otherGroupAssert = dependenciesAssert.getElement(1)
		otherGroupAssert.assertField('name', 'Other')
		otherGroupAssert.hasNoField('description')
		otherGroupAssert.assertRootSize('content', 2)
		JsonAssert otherDependencies = otherGroupAssert.getChild('content')
		assertDependency(otherDependencies.getElement(0), 'org.acme:foo:1.3.5', 'Foo', null)
		assertDependency(otherDependencies.getElement(1), 'org.acme:bar:2.1.0', 'Bar', null)

		rootAssert.assertRootSize('types', 4)
		rootAssert.assertRootSize('packagings', 2)
		rootAssert.assertRootSize('javaVersions', 3)
		rootAssert.assertRootSize('languages', 2)
		rootAssert.assertRootSize('bootVersions', 3)
	}

	@Test
	void homeIsForm() {
		String body = home()
		assertTrue 'Wrong body:\n' + body, body.contains('action="/starter.zip"')
	}

	@Test
	void webIsAddedPom() {
		String body = restTemplate.getForObject(createUrl('/pom.xml?packaging=war'), String)
		assertTrue('Wrong body:\n' + body, body.contains('spring-boot-starter-web'))
		assertTrue('Wrong body:\n' + body, body.contains('provided'))
	}

	@Test
	void webIsAddedGradle() {
		String body = restTemplate.getForObject(createUrl('/build.gradle?packaging=war'), String)
		assertTrue('Wrong body:\n' + body, body.contains('spring-boot-starter-web'))
		assertTrue('Wrong body:\n' + body, body.contains('providedRuntime'))
	}

	@Test
	void infoHasExternalProperties() {
		String body = restTemplate.getForObject(createUrl('/info'), String)
		assertTrue('Wrong body:\n' + body, body.contains('"spring-boot"'))
		assertTrue('Wrong body:\n' + body, body.contains('"version":"1.1.5.RELEASE"'))
	}

	@Test
	void homeHasWebStyle() {
		String body = home()
		assertTrue('Wrong body:\n' + body, body.contains('name="style" value="web"'))
	}

	@Test
	void homeHasBootVersion() {
		String body = home()
		assertTrue('Wrong body:\n' + body, body.contains('name="bootVersion"'))
		assertTrue('Wrong body:\n' + body, body.contains('1.2.0.BUILD-SNAPSHOT"'))
	}

	@Test
	void downloadStarter() {
		byte[] body = restTemplate.getForObject(createUrl('starter.zip'), byte[])
		assertNotNull(body)
		assertTrue(body.length > 100)
	}

	@Test
	void installer() {
		ResponseEntity<String> response = restTemplate.getForEntity(createUrl('install.sh'), String)
		assertEquals(HttpStatus.OK, response.getStatusCode())
		assertNotNull(response.body)
	}

	private static void assertDependency(JsonAssert jsonAssert, String id, String name, String description) {
		jsonAssert.assertField('id', id)
		jsonAssert.assertField('name', name)
		if (description != null) {
			jsonAssert.assertField('description', description)
		} else {
			jsonAssert.hasNoField('description')
		}
		jsonAssert.hasNoField('groupId', 'artifactId', 'version')
	}

	private String home() {
		HttpHeaders headers = new HttpHeaders()
		headers.setAccept([MediaType.TEXT_HTML])
		restTemplate.exchange(createUrl('/'), HttpMethod.GET, new HttpEntity<Void>(headers), String).body
	}

	private String createUrl(String context) {
		return 'http://localhost:' + port + context
	}
}
