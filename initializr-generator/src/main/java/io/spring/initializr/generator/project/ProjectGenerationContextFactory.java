/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.generator.project;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * @author Stephane Nicoll
 */
public class ProjectGenerationContextFactory implements Supplier<ProjectGenerationContext> {

	private final ProjectDescription projectDescription;

	private Supplier<? extends ProjectGenerationContext> contextFactory;

	private Predicate<String> projectGenerationConfigurationFilter;

	private Consumer<ProjectGenerationContext> contextConsumer;

	protected ProjectGenerationContextFactory(ProjectDescription projectDescription) {
		this.projectDescription = projectDescription;
		this.contextFactory = defaultContextFactory();
	}

	private static Supplier<ProjectGenerationContext> defaultContextFactory() {
		return () -> {
			ProjectGenerationContext context = new ProjectGenerationContext();
			context.setAllowBeanDefinitionOverriding(false);
			return context;
		};
	}

	@Override
	public ProjectGenerationContext get() {
		return createProjectGenerationContext();
	}

	protected ProjectGenerationContext createProjectGenerationContext() {
		ProjectGenerationContext context = this.contextFactory.get();
		context.registerBean(ProjectDescription.class, resolveProjectDescription(context));
		registerProjectGenerationConfigurations(context);
		if (this.contextConsumer != null) {
			this.contextConsumer.accept(context);
		}
		return context;
	}

	/**
	 * Initialize a {@link ProjectGenerationContextFactory} for the specified
	 * {@link ProjectDescription}.
	 * @param projectDescription the description of the project to generate
	 * @return a {@link ProjectGenerationContextFactory}.
	 */
	public static ProjectGenerationContextFactory of(ProjectDescription projectDescription) {
		return new ProjectGenerationContextFactory(projectDescription);
	}

	public ProjectGenerationContextFactory withContextFactory(
			Supplier<? extends ProjectGenerationContext> contextFactory) {
		this.contextFactory = contextFactory;
		return this;
	}

	public ProjectGenerationContextFactory withProjectGenerationConfigurationFilter(Predicate<String> filter) {
		this.projectGenerationConfigurationFilter = filter;
		return this;
	}

	public ProjectGenerationContextFactory withContextCustomizer(Consumer<ProjectGenerationContext> contextConsumer) {
		this.contextConsumer = contextConsumer;
		return this;
	}

	private Supplier<ProjectDescription> resolveProjectDescription(ProjectGenerationContext context) {
		return () -> {
			if (this.projectDescription instanceof MutableProjectDescription) {
				MutableProjectDescription mutableDescription = (MutableProjectDescription) this.projectDescription;
				ProjectDescriptionDiffFactory diffFactory = context.getBeanProvider(ProjectDescriptionDiffFactory.class)
						.getIfAvailable(DefaultProjectDescriptionDiffFactory::new);
				// Create the diff here so that it takes a copy of the description
				// immediately
				ProjectDescriptionDiff diff = diffFactory.create(mutableDescription);
				context.registerBean(ProjectDescriptionDiff.class, () -> diff);
				context.getBeanProvider(ProjectDescriptionCustomizer.class).orderedStream()
						.forEach((customizer) -> customizer.customize(mutableDescription));
			}
			return this.projectDescription;
		};
	}

	private void registerProjectGenerationConfigurations(ProjectGenerationContext context) {
		List<String> factories = SpringFactoriesLoader.loadFactoryNames(ProjectGenerationConfiguration.class,
				getClass().getClassLoader());
		Predicate<String> filter = (this.projectGenerationConfigurationFilter != null)
				? this.projectGenerationConfigurationFilter : (String) -> true;
		Arrays.stream(factories.toArray(new String[0])).filter(filter).forEach((configurationClassName) -> {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClassName(configurationClassName);
			context.registerBeanDefinition(configurationClassName, beanDefinition);
		});
	}

}
