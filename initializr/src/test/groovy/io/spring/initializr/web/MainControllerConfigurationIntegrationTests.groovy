/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web

import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.metadata.InitializrMetadataProvider
import org.junit.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.UrlResource
import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerConfigurationIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Autowired
	private InitializrMetadataProvider metadataProvider

	@Test
	void initializeRemoteConfig() {
		InitializrMetadata localMetadata = metadataProvider.get()
		InitializrMetadata metadata = InitializrMetadataBuilder.create().withInitializrMetadata(
				new UrlResource(createUrl('/config'))).build()
		// Basic assertions
		assertEquals(localMetadata.dependencies.content.size(), metadata.dependencies.content.size())
		assertEquals(localMetadata.types.content.size(), metadata.types.content.size())
		assertEquals(localMetadata.bootVersions.content.size(), metadata.bootVersions.content.size())
		assertEquals(localMetadata.packagings.content.size(), metadata.packagings.content.size())
		assertEquals(localMetadata.javaVersions.content.size(), metadata.javaVersions.content.size())
		assertEquals(localMetadata.languages.content.size(), metadata.languages.content.size())
	}

}
