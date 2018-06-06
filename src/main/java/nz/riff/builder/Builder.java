package nz.riff.builder;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.riff.builder.bean.Song;
import nz.riff.builder.repository.ChordRepository;
import nz.riff.builder.repository.SongRepository;
import nz.riff.writer.ChordWriter;
import nz.riff.writer.Cleaner;
import nz.riff.writer.SongWriter;

public class Builder {

	private static final Logger log = LoggerFactory.getLogger(Builder.class);

	static final String OUTPUT_JSON_DIR = "./output/json/";

	private ChordRepository chordRepository;

	private SongRepository songRepository;

	private SongWriter songWriter;

	private ChordWriter chordWriter;

	private Cleaner cleaner;

	@Inject
	public Builder(ChordRepository chordRepository, SongRepository songRepository, SongWriter songWriter, ChordWriter chordWriter, Cleaner cleaner) {
		super();
		this.chordRepository = chordRepository;
		this.songRepository = songRepository;
		this.songWriter = songWriter;
		this.chordWriter = chordWriter;
		this.cleaner = cleaner;
	}

	public void build() {
		try {
			Map<String, Song> songs = songRepository.get();
			this.cleaner.clean(OUTPUT_JSON_DIR);
			this.songWriter.write(OUTPUT_JSON_DIR, songs);
			this.chordWriter.write(OUTPUT_JSON_DIR, chordRepository.getAllChords());
		} catch (Exception e) {
			log.info("Problem when building data: ", e);
		}
	}
}
