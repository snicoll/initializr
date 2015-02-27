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

package io.spring.initializr.metadata

import org.springframework.web.client.RestTemplate

/**
 *
 * @author Stephane Nicoll
 */
class InitializrMetadataBuilder<T extends InitializrMetadata> {

	private T initializrMetadata = createInstance()

	// Service exposes an additional endpoint that exposes the "full" configuration. So
	// it can be reused to load the config from an existing service
	InitializrMetadataBuilder fromService(String url) {
		String json = new RestTemplate().getForObject(url, String.class)
		InitializrMetadata m = load(json)
		initializrMetadata.merge(m)
		this
	}

	// Other utility methods - load config from classpath or whatever


	protected T createInstance() {
		new InitializrMetadata() // far fetched? Create your own override
	}

	protected T load(String json) {
		// Use jackson to load the object
	}


	InitializrMetadata build() {
		return initializrMetadata
	}



}
