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

/**
 *
 * @author Stephane Nicoll
 */
class ServiceCapability<T> {

	static final String TYPE_TEXT = 'text'

	static final String TYPE_SINGLE_SELECT = 'single-select'

	static final String TYPE_HIERARCHICAL_MULTI_SELECT = 'hierarchical-multi-select'

	final String id

	final String type

	String description

	T content

	protected ServiceCapability(String id, String type) {
		this.id = id
		this.type = type
	}
}
