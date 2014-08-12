package io.spring.initializr

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * @author Stephane Nicoll
 */
@EnableAutoConfiguration
class TestApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TestApp.class)
		app.additionalProfiles = 'test-prod'
		app.run(args)
	}
}
