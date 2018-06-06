package nz.riff.builder.parsers;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.riff.builder.bean.Chord;

public class ChordParser {

	private ObjectMapper yaml;

	@Inject
	public ChordParser(@Named("yaml") ObjectMapper yaml) {
		super();
		this.yaml = yaml;
	}

	public Chord parse(File file) throws ChordParsingException {
		try {
			return yaml.readValue(file, Chord.class);
		} catch (Exception e) {
			throw new ChordParsingException("Problem when parsing chord from file '" + file + "'", e);
		}
	}
}
