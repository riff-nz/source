package nz.riff.writer;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.riff.builder.bean.Chord;

public class ChordWriter {

	private static final Logger log = LoggerFactory.getLogger(ChordWriter.class);

	private ObjectMapper mapper;

	@Inject
	public ChordWriter(@Named("json") ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}

	public void write(String outputDir, Map<String, Chord> chords) {
		for (Chord chord : chords.values()) {
			this.write(outputDir + "chords/" + chord.key, chord);
		}
		try {
			mapper.writeValue(new File(outputDir + "chords/index.json"), chords.values());
		} catch (Exception e) {
			log.error("Problen when writing chord index", e);
		}
	}

	public void write(String path, Chord chord) {
		try {
			File directory = new File(path.substring(0, path.lastIndexOf("/")));
			directory.mkdirs();
			mapper.writeValue(new File(path + ".json"), chord);
		} catch (Exception e) {
			log.error("Problen when writing chord", e);
		}
	}
}
