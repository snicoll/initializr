/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSettings.Builder;

/**
 * Gradle build configuration for a project.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
public class GradleBuild extends Build {

	private final GradleBuildSettings.Builder settings = new Builder();

	private final GradlePluginContainer plugins = new GradlePluginContainer();

	private final GradleConfigurationContainer configurations = new GradleConfigurationContainer();

	private final GradleTaskContainer tasks = new GradleTaskContainer();

	private final Buildscript buildscript = new Buildscript();

	public GradleBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
	}

	public GradleBuild() {
		this(null);
	}

	@Override
	public GradleBuildSettings.Builder settings() {
		return this.settings;
	}

	@Override
	public GradleBuildSettings getSettings() {
		return this.settings.build();
	}

	public GradlePluginContainer plugins() {
		return this.plugins;
	}

	public GradleConfigurationContainer configurations() {
		return this.configurations;
	}

	public GradleTaskContainer tasks() {
		return this.tasks;
	}

	public void buildscript(Consumer<Buildscript> customizer) {
		customizer.accept(this.buildscript);
	}

	public Buildscript getBuildscript() {
		return this.buildscript;
	}

	/**
	 * The {@code buildscript} block in the {@code build.gradle} file.
	 */
	public static class Buildscript {

		private final List<String> dependencies = new ArrayList<>();

		private final Map<String, String> ext = new LinkedHashMap<>();

		public Buildscript dependency(String coordinates) {
			this.dependencies.add(coordinates);
			return this;
		}

		public Buildscript ext(String key, String value) {
			this.ext.put(key, value);
			return this;
		}

		public List<String> getDependencies() {
			return Collections.unmodifiableList(this.dependencies);
		}

		public Map<String, String> getExt() {
			return Collections.unmodifiableMap(this.ext);
		}

	}

}
