import java.text.SimpleDateFormat

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory

import sun.nio.ch.IOUtil

import com.thebuzzmedia.exiftool.ExifTool
import com.thebuzzmedia.exiftool.ExifTool.Tag

def LOG = LoggerFactory.getLogger("groovy-exif")
def tool = new ExifTool();

def parseFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss")
def renderFormat = new SimpleDateFormat("MM-MMM-yyyy")
def cal = Calendar.getInstance()

getFolderName = { datetimeString ->

	if (null != datetimeString) {
		renderFormat.format(parseFormat.parse(datetimeString))
	} else {
		"NoExifData"
	}
}

if (this.args.size() == 0) {
	LOG.error("Need a path to the folder to process!")
} else {
	def count = 0
	def path = args[0]
	LOG.warn("Processing [{}]", path)

	new File( path ).eachFile { source ->

		if (source.getName().endsWith("jpg") || source.getName().endsWith("JPG")) {
			Map<Tag, String> valueMap =
					tool.getImageMeta(source, Tag.DATE_TIME_ORIGINAL)

			def folderName = getFolderName(valueMap.get(Tag.DATE_TIME_ORIGINAL))

			LOG.debug("src [{}] DATE_TIME_ORIGINAL [{}] folderName [{}]", source.getAbsolutePath(), valueMap.get(Tag.DATE_TIME_ORIGINAL), folderName)

			def dir = new File(path + "/" + folderName)
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new RuntimeException("Unable to create directory [" + dir.getAbsolutePath() + "]")
				}
			}

			def File destination = new File(dir.getAbsolutePath() + "/" + source.getName())
			if (!destination.exists()) {
				if (!destination.createNewFile()) {
					throw new RuntimeException("Unable to create file [" + destination.getAbsolutePath() + "]")
				}
			}

			LOG.debug("Copy [{}] to [{}]", source.getAbsolutePath(), destination.getAbsolutePath())

			def src = new FileInputStream(source)
			def dest = new FileOutputStream(destination)
			IOUtils.copy(src, dest)
			dest.close()
			src.close()

			LOG.warn("Delete [{}]", source.getAbsolutePath())

			if (!source.delete()) {
				throw new RuntimeException("Unable to remove [" + source.getAbsolutePath() + "]")
			}
			count++
		}
	}
	LOG.warn("Found [{}] JPEG files", count)
}

