package io.spring.initializr.web

import java.nio.charset.Charset

import io.spring.initializr.support.ProjectAssert
import io.spring.initializr.support.ZipUtils
import org.apache.tools.ant.taskdefs.GUnzip
import org.json.JSONObject
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.StreamUtils

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerIntegrationTests extends AbstractMainControllerIntegrationTests {

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
	public void simpleZipProject() {
		downloadZip('/starter.zip?style=web&style=jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa') // alias jpa -> data-jpa
				.hasSpringBootStarterDependency('test')
	}

	@Test
	public void simpleTgzProject() {
		downloadTgz('/starter.tgz?style=org.acme:bar:2.1.0').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasDependency('org.acme', 'bar', '2.1.0')
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

	private ProjectAssert downloadZip(String context) {
		byte[] body = restTemplate.getForObject(createUrl(context), byte[])
		def project = folder.newFolder()
		ZipUtils.unzip(new ByteArrayInputStream(body), project)
		new ProjectAssert(project)
	}

	private ProjectAssert downloadTgz(String context) {
		byte[] body = restTemplate.getForObject(createUrl(context), byte[])
		// Write the TGZ somewhere
		def tgzFile = folder.newFile()
		def stream = new FileOutputStream(tgzFile)
		try {
			stream.write(body)
		} finally {
			stream.close()
		}

		def project = folder.newFolder()
		new AntBuilder().untar(dest: project, src:tgzFile , compression: 'gzip');
		new ProjectAssert(project)
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
}
