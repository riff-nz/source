package nz.riff.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import nz.riff.builder.bean.Song;

public class SongPdfWriter {

	private static final Logger log = LoggerFactory.getLogger(SongPdfWriter.class);

	public void write(String outputDir, String htmlTemplate, Map<String, Song> songs) {
		for (Entry<String, Song> entry : songs.entrySet()) {
			this.write(outputDir + "songs/" + entry.getKey(), htmlTemplate, entry.getValue());
		}
	}

	public void write(String path, String htmlTemplate, Song song) {
		try {
			String content = htmlTemplate;
			content = content.replace("${title}", song.artist + " - " + song.name);
			content = content.replace("${name}", song.name);
			content = content.replace("${artist}", song.artist);
			content = content.replace("${content}", song.html);
			File directory = new File(path.substring(0, path.lastIndexOf("/")));
			directory.mkdirs();

			FileUtils.writeStringToFile(new File(path + ".html"), content, "UTF-8");

			FileOutputStream outputStream = new FileOutputStream(path + ".pdf");
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(content);
			renderer.layout();
			renderer.createPDF(outputStream);
			outputStream.close();

			// FileUtils.writeStringToFile(new File(path + ".pdf"), content, "UTF-8");
		} catch (Exception e) {
			log.error("Problen when writing song", e);
		}
	}
}
