package nz.riff.builder.parsers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.StringUtils;

import nz.riff.builder.bean.Chord;
import nz.riff.builder.bean.Song;
import nz.riff.builder.repository.ChordRepository;

public class SongParser {

	private static final Logger log = LoggerFactory.getLogger(SongParser.class);

	private ObjectMapper yaml;

	private ChordRepository chordRepository;

	private ChordLineParser chordLineParser;

	private LyricsAndChordsMerger lyricsAndChordsMerger;

	@Inject
	public SongParser(@Named("yaml") ObjectMapper yaml, ChordRepository chordRepository, ChordLineParser chordLineParser, LyricsAndChordsMerger lyricsAndChordsMerger) {
		super();
		this.yaml = yaml;
		this.chordRepository = chordRepository;
		this.chordLineParser = chordLineParser;
		this.lyricsAndChordsMerger = lyricsAndChordsMerger;
	}

	public Song parse(String data) throws ChordParsingException {
		Song song = new Song();
		try {
			String[] parts = data.split("---");

			song = yaml.readValue(parts[0], Song.class);

			Set<String> allSongChords = new HashSet<>();
			song.html = this.parseBody(parts[1], allSongChords);
			song.chords = this.getSongChords(this.chordRepository.getChordsForSong(song.chords), allSongChords);

			return song;
		} catch (Exception e) {
			throw new ChordParsingException("Problem when parsing song", e);
		}
	}

	List<Chord> getSongChords(Map<String, Chord> allChords, Set<String> allSongChords) throws ChordParsingException {
		List<Chord> chords = new ArrayList<>();

		for (String chord : allSongChords) {
			if (!allChords.containsKey(chord)) {
				throw new ChordParsingException("Can't find chord '" + chord + "'");
			}
			chords.add(allChords.get(chord));
		}
		return chords;
	}

	String parseBody(String body, Set<String> allSongChords) {
		StringBuilder data = new StringBuilder();
		String[] lines = body.trim().split(System.getProperty("line.separator"));
		int lineNumber = 0;
		while (lineNumber < lines.length) {
			String lyrics = null;
			String chords = null;

			// parse heading
			if (lines[lineNumber].startsWith("# ")) {
				this.addSection(data, lines[lineNumber].substring(2));
				lineNumber++;
				continue;
			}

			if (lines[lineNumber].startsWith("% ")) {
				chords = lines[lineNumber].substring(2);
				lineNumber++;
			}

			if (lineNumber < lines.length && lines[lineNumber].startsWith("> ")) {
				lyrics = lines[lineNumber].substring(2);
				lineNumber++;
			}

			if (lyrics != null || chords != null) {
				Map<Integer, List<String>> parsedChords = this.chordLineParser.parse(chords);
				this.addChords(allSongChords, parsedChords);
				this.addLine(data, this.lyricsAndChordsMerger.merge(lyrics, parsedChords), lyrics != null ? "with-lyrics" : "chords-only");
				continue;
			}

			this.addLine(data, lines[lineNumber]);

			lineNumber++;
		}
		
		if (data.length() > 0) {
			data.append("</div>");
		}

		return data.toString();
	}

	void addChords(Set<String> allSongChords, Map<Integer, List<String>> parsedChords) {
		for (List<String> values : parsedChords.values()) {
			allSongChords.addAll(values);
		}
	}

	void addSection(StringBuilder data, String line) {
		if (data.length() > 0) {
			data.append("</div>");
		}
		String className = line.contains(" ") ? line.toLowerCase().substring(0, line.indexOf(" ")) : line.toLowerCase();
		data.append("<div class=\"section " + className + "\">");
		data.append("<h3>");
		data.append(line);
		data.append("</h3>");
	}

	void addLine(StringBuilder data, String line, String ... classNAmes) {
		String cssClasses = "line";
		if (classNAmes != null && classNAmes.length > 0) {
			for (String cl : classNAmes) {
				cssClasses += (cl == null || "".equals(cl) ? "" : " " + cl);
			}
		}
		data.append("<div class=\"" + cssClasses + "\">");
		data.append(line);
		data.append("</div>");
	}
}
