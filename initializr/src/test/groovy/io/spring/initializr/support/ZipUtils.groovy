package io.spring.initializr.support

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 *
 * @author Stephane Nicoll
 */
class ZipUtils {

	private static final int BUFFER_SIZE = 4096

	static def unzip(InputStream input, File targetDirectory) {
		if (!targetDirectory.exists()) {
			targetDirectory.mkdir()
		}
		ZipInputStream zipIn = new ZipInputStream(input)
		ZipEntry entry = zipIn.nextEntry
		while (entry != null) {
			String filePath = targetDirectory.absolutePath + File.separator + entry.name

			File file = new File(filePath)
			if (!entry.directory) {
				file.createNewFile()
				extractFile(zipIn, file)
			} else {
				file.mkdir()
			}
			zipIn.closeEntry()
			entry = zipIn.nextEntry
		}
		zipIn.close()
	}


	private static void extractFile(ZipInputStream zipIn, File file) {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))
		byte[] bytesIn = new byte[BUFFER_SIZE]
		int read = 0
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read)
		}
		bos.close()
	}
}
