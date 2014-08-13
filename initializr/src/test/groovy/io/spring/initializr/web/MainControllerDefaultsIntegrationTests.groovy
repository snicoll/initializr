package io.spring.initializr.web

import io.spring.initializr.support.PomAssert
import org.junit.Test

import org.springframework.test.context.ActiveProfiles

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles(['test-default', 'test-defaults-customization'])
class MainControllerDefaultsIntegrationTests extends AbstractMainControllerIntegrationTests {

	@Test
	void generateDefaultPom() { // see defaults customization
		String content = restTemplate.getForObject(createUrl('/pom.xml?style=web'), String)
		PomAssert pomAssert = new PomAssert(content)
		pomAssert.hasGroupId('org.foo').hasArtifactId('foo-bar').hasVersion('1.2.4-SNAPSHOT').hasPackaging('jar')
				.hasName('FooBar').hasDescription('FooBar Project').hasStartClass('org.foo.demo.Application')
	}

}
