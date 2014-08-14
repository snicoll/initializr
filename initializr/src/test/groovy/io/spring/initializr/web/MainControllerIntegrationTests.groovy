package io.spring.initializr.web

import java.nio.charset.Charset

import io.spring.initializr.support.PomAssert
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.util.StreamUtils
import org.springframework.web.client.RestTemplate

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@WebAppConfiguration
@ActiveProfiles('test-default')
@IntegrationTest('server.port=0')
class MainControllerIntegrationTests {

	@Value('${local.server.port}')
	private int port

	private final RestTemplate restTemplate = new RestTemplate()

	@Test
	// Test that the current output is exactly what we expect
	public void validateCurrentProjectMetadata() {
		String json = restTemplate.getForObject(createUrl('/'), String.class)
		JSONObject expected = readJson('1.0')
		JSONAssert.assertEquals(expected, new JSONObject(json), JSONCompareMode.STRICT)
	}

	@Test
	// Test that the  current code complies "at least" with 1.0
	public void validateProjectMetadata10() {
		String json = restTemplate.getForObject(createUrl('/'), String.class)
		JSONObject expected = readJson('1.0')
		JSONAssert.assertEquals(expected, new JSONObject(json), JSONCompareMode.LENIENT)
	}


	@Test
	void generateDefaultPom() { // see defaults customization
		String content = restTemplate.getForObject(createUrl('/pom.xml?style=web'), String)
		PomAssert pomAssert = new PomAssert(content)
		pomAssert.hasGroupId('org.foo').hasArtifactId('foo-bar').hasVersion('1.2.4-SNAPSHOT')
				.hasName('FooBar').hasDescription('FooBar Project').hasStartClass('org.foo.demo.Application')
	}

	// Existing tests for backward compatibility

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

	private String home() {
		HttpHeaders headers = new HttpHeaders()
		headers.setAccept([MediaType.TEXT_HTML])
		restTemplate.exchange(createUrl('/'), HttpMethod.GET, new HttpEntity<Void>(headers), String).body
	}

	private String createUrl(String context) {
		return 'http://localhost:' + port + context
	}

	private static JSONObject readJson(String version) {
		def resource = new ClassPathResource('metadata/test-default-' + version + '.json')
		def stream = resource.getInputStream()
		try {
			String json = StreamUtils.copyToString(stream, Charset.forName('UTF-8'))
			new JSONObject(json)
		} finally {
			stream.close()
		}
	}


	@EnableAutoConfiguration
	static class Config {

	}
}
