package io.spring.initializr.web

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine
import io.spring.initializr.ProjectMetadata
import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
@Controller
class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class)

	@Value('${TMPDIR:.}')
	private String tmpdir

	@Value('${info.spring-boot.version}')
	private String bootVersion

	@Autowired
	private ProjectMetadata projects

	@ModelAttribute
	ProjectRequest projectRequest() {
		ProjectRequest request = new ProjectRequest()
		request.bootVersion = bootVersion
		request
	}

	@RequestMapping(value = "/")
	@ResponseBody
	ProjectMetadata projects() {
		projects
	}

	@RequestMapping(value = '/', produces = 'text/html')
	@ResponseBody
	String home() {
		def model = [:]
		projects.properties.each { model[it.key] = it.value }
		template 'home.html', model
	}

	@RequestMapping('/pom')
	@ResponseBody
	ResponseEntity<byte[]> pom(ProjectRequest request, Map model) {
		model.bootVersion = request.bootVersion
		new ResponseEntity<byte[]>(render('starter-pom.xml', request, model), ['Content-Type':'application/octet-stream'] as HttpHeaders, HttpStatus.OK)

	}

	@RequestMapping('/build')
	@ResponseBody
	ResponseEntity<byte[]> gradle(ProjectRequest request, Map model) {
		model.bootVersion = request.bootVersion
		new ResponseEntity<byte[]>(render('starter-build.gradle', request, model), ['Content-Type':'application/octet-stream'] as HttpHeaders, HttpStatus.OK)
	}

	byte[] render(String path, ProjectRequest request, Map model) {
		if (request.packaging=='war' && !request.isWebStyle()) {
			request.style << 'web'
		}
		logger.info("Styles requested: ${request.style}, Type requested: ${request.type}")
		request.properties.each { model[it.key] = it.value }
		model.styles = fixStyles(request.style)
		template path, model
	}

	@RequestMapping('/starter.zip')
	@ResponseBody
	ResponseEntity<byte[]> springZip(ProjectRequest request) {
		def dir = getProjectFiles(request)

		File download = new File(tmpdir, dir.name + '.zip')
		addTempFile(dir.name, download)

		new AntBuilder().zip(destfile: download) {
			zipfileset(dir:dir, includes:'**')
		}
		logger.info("Uploading: ${download} (${download.bytes.length} bytes)")
		def result = new ResponseEntity<byte[]>(download.bytes,
				['Content-Type':'application/zip'] as HttpHeaders, HttpStatus.OK)

		cleanTempFiles(dir.name)

		result
	}

	def getProjectFiles(ProjectRequest request) {

		def model = [:]

		File dir = File.createTempFile('tmp','',new File(tmpdir))
		addTempFile(dir.name, dir)
		dir.delete()
		dir.mkdirs()

		if (request.type.contains('gradle')) {
			String gradle = new String(gradle(request, model).body)
			new File(dir, 'build.gradle').write(gradle)
		} else {
			String pom = new String(pom(request, model).body)
			new File(dir, 'pom.xml').write(pom)
		}

		String language = request.language

		File src = new File(new File(dir, 'src/main/' + language),request.packageName.replace('.', '/'))
		src.mkdirs()
		write(src, 'Application.' + language, model)

		if (request.packaging=='war') {
			write(src, 'ServletInitializer.' + language, model)
		}

		File test = new File(new File(dir, 'src/test/' + language),request.packageName.replace('.', '/'))
		test.mkdirs()
		if (model.styles.contains('-web')) {
			model.testAnnotations = '@WebAppConfiguration\n'
			model.testImports = 'import org.springframework.test.context.web.WebAppConfiguration;\n'
		} else {
			model.testAnnotations = ''
			model.testImports = ''
		}
		write(test, 'ApplicationTests.' + language, model)

		File resources = new File(dir, 'src/main/resources')
		resources.mkdirs()
		new File(resources, 'application.properties').write('')

		if (request.isWebStyle()) {
			new File(dir, 'src/main/resources/templates').mkdirs()
			new File(dir, 'src/main/resources/static').mkdirs()
		}

		dir

	}

	def write(File src, String name, def model) {
		String tmpl = name.endsWith('.groovy') ? name + '.tmpl' : name
		def body = template tmpl, model
		new File(src, name).write(body)
	}

	private def fixStyles(def style) {
		if (style==null || style.size()==0) {
			style = ['']
		}
		if (!style.class.isArray() && !(style instanceof Collection)) {
			style = [style]
		}
		style = style.collect{ it=='jpa' ? 'data-jpa' : it }
		style.collect{ it=='' ? '' : '-' + it }
	}

	private void addTempFile(String group, File file) {
		//reactor.notify('/temp/' + group, Event.wrap(file))
	}

	private void cleanTempFiles(String group) {
		//reactor.notify('/clean/' + group)
	}




	// This should not be here - copy/paste from the cli

	public static String template(String name, Map<String, ?> model) throws IOException,
			CompilationFailedException, ClassNotFoundException {
		return template(new GStringTemplateEngine(), name, model)
	}

	public static String template(TemplateEngine engine, String name, Map<String, ?> model)
			throws IOException, CompilationFailedException, ClassNotFoundException {
		Writable writable = getTemplate(engine, name).make(model)
		StringWriter result = new StringWriter()
		writable.writeTo(result)
		return result.toString()
	}

	private static Template getTemplate(TemplateEngine engine, String name)
			throws CompilationFailedException, ClassNotFoundException, IOException {

		File file = new File("templates", name)
		if (file.exists()) {
			return engine.createTemplate(file)
		}

		ClassLoader classLoader = MainController.class.getClassLoader()
		URL resource = classLoader.getResource("templates/" + name)
		if (resource != null) {
			return engine.createTemplate(resource)
		}

		return engine.createTemplate(name)
	}
}
