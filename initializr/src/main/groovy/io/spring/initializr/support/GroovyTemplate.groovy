package io.spring.initializr.support

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine
import org.codehaus.groovy.control.CompilationFailedException

/**
 *
 * @author Dave Syer
 */
class GroovyTemplate {

	// This should not be here - copy/paste from the cli

	static String template(String name, Map<String, ?> model) throws IOException,
			CompilationFailedException, ClassNotFoundException {
		return template(new GStringTemplateEngine(), name, model)
	}

	static String template(TemplateEngine engine, String name, Map<String, ?> model)
			throws IOException, CompilationFailedException, ClassNotFoundException {
		Writable writable = getTemplate(engine, name).make(model)
		StringWriter result = new StringWriter()
		writable.writeTo(result)
		return result.toString()
	}

	static Template getTemplate(TemplateEngine engine, String name)
			throws CompilationFailedException, ClassNotFoundException, IOException {

		File file = new File("templates", name)
		if (file.exists()) {
			return engine.createTemplate(file)
		}

		ClassLoader classLoader = GroovyTemplate.class.getClassLoader()
		URL resource = classLoader.getResource("templates/" + name)
		if (resource != null) {
			return engine.createTemplate(resource)
		}

		return engine.createTemplate(name)
	}
}
