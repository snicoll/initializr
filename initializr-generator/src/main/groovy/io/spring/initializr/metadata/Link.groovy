/*
 * Copyright 2012-2017 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.ToString

/**
 * Meta-data for a link. Each link has a "relation" that potentially attaches a strong
 * semantic to the nature of the link.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
@ToString(ignoreNulls = true, includePackage = false)
class Link {

	/**
	 * The relation of the link.
	 */
	String rel;

	/**
	 * The URI the link is pointing to.
	 */
	String href

	/**
	 * Specify if the URI is templated.
	 */
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	boolean templated

	/**
	 * A description of the link.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String description

	void setHref(String href) {
		this.href = href

	}

	void resolve() {
		if (!rel) {
			throw new InvalidInitializrMetadataException(
					"Invalid link $this: rel attribute is mandatory")
		}
		if (!href) {
			throw new InvalidInitializrMetadataException(
					"Invalid link $this: href attribute is mandatory")
		}
	}

	protected void setTemplated(boolean templated) {
		this.templated = templated
	}
}
