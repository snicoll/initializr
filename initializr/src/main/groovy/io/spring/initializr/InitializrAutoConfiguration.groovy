package io.spring.initializr

import io.spring.initializr.web.MainController

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Stephane Nicoll
 */
@Configuration
@EnableConfigurationProperties(ProjectMetadata.class)
class InitializrAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(MainController.class)
	MainController initializrMainController() {
		new MainController()
	}
}
