package nz.riff.builder;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.riff.builder.bean.Song;
import nz.riff.builder.repository.ChordRepository;
import nz.riff.builder.repository.SongRepository;
import nz.riff.writer.ChordJSonWriter;
import nz.riff.writer.Cleaner;
import nz.riff.writer.SongJsonWriter;
import nz.riff.writer.SongPdfWriter;

public class Builder {

	private static final Logger log = LoggerFactory.getLogger(Builder.class);

	static final String OUTPUT_JSON_DIR = "./output/json/";

	static final String OUTPUT_PDF_DIR = "./output/pdf/";

	private ChordRepository chordRepository;

	private SongRepository songRepository;

	private SongJsonWriter songJsonWriter;

	private SongPdfWriter songPdfWriter;

	private ChordJSonWriter chordJsonWriter;

	private Cleaner cleaner;

	@Inject
	public Builder(ChordRepository chordRepository, SongRepository songRepository, SongJsonWriter songJsonWriter,
			SongPdfWriter songPdfWriter, ChordJSonWriter chordJsonWriter, Cleaner cleaner) {
		super();
		this.chordRepository = chordRepository;
		this.songRepository = songRepository;
		this.songJsonWriter = songJsonWriter;
		this.songPdfWriter = songPdfWriter;
		this.chordJsonWriter = chordJsonWriter;
		this.cleaner = cleaner;
	}

	public void build() {
		try {
			Map<String, Song> songs = songRepository.get();
			this.cleaner.clean(OUTPUT_JSON_DIR);
			this.songJsonWriter.write(OUTPUT_JSON_DIR, songs);
			this.chordJsonWriter.write(OUTPUT_JSON_DIR, chordRepository.getAllChords());

			this.writePdf(songs);
		} catch (Exception e) {
			log.info("Problem when building data: ", e);
		}
	}

	void writePdf(Map<String, Song> songs) {
		try {
			String htmlTemplate = FileUtils.readFileToString(new File("./data/template.html"), "UTF-8");
			this.songPdfWriter.write(OUTPUT_PDF_DIR, htmlTemplate, songs);
		} catch (Exception e) {
			log.error("Problem when writing PDFs", e);
		}
	}

}
