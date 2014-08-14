package io.spring.initializr.web

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate

/**
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@WebAppConfiguration
@IntegrationTest('server.port=0')
abstract class AbstractMainControllerIntegrationTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	@Value('${local.server.port}')
	private int port

	final RestTemplate restTemplate = new RestTemplate()

	String createUrl(String context) {
		return 'http://localhost:' + port + context
	}

	@EnableAutoConfiguration
	static class Config {}
}
