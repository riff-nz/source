package nz.riff.builder.repository;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.riff.builder.bean.Chord;
import nz.riff.builder.parsers.ChordParser;

@Singleton
public class ChordRepository {

	private static final Logger log = LoggerFactory.getLogger(ChordRepository.class);

	private ChordParser chordParser;

	private Map<String, Chord> chords;

	@Inject
	public ChordRepository(ChordParser chordParser) {
		super();
		this.chordParser = chordParser;
	}

	void load() {
		log.debug("Loading chords...");
		chords = new LinkedHashMap<>();

		File folder = new File("../src/chords/");
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				try {
					Chord chord = this.chordParser.parse(file);
					chord.key = this.getKey(chord);
					this.add(chords, chord);
				} catch (Exception e) {
					log.error("Problem when parsing chord", e);
				}
			}
		}
		log.debug("Loaded {} chords...", this.chords.size());
	}

	public Map<String, Chord> getAllChords() {
		if (this.chords == null) {
			this.load();
		}

		return new LinkedHashMap<>(this.chords);
	}

	public Map<String, Chord> getChordsForSong(List<Chord> songChords) {
		Map<String, Chord> chordsForSong = new LinkedHashMap<>(this.getAllChords());
		if (songChords != null) {
			for (Chord chord : songChords) {
				this.add(chordsForSong, chord);
			}
		}
		return chordsForSong;
	}

	String getKey(Chord chord) {
		String key = chord.name;
		key = key.replaceAll("#", "-sharp-");
		key = key.replaceAll("/", "-");
		if (key.endsWith("-")) {
			key = key.substring(0, key.length() - 1);
		}
		return key;
	}

	void add(Map<String, Chord> chords, Chord chord) {
		chords.put(chord.name, chord);
	}
}
