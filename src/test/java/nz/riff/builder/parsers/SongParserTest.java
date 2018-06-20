package nz.riff.builder.parsers;

import java.util.LinkedHashSet;
import java.util.Set;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.riff.builder.repository.ChordRepository;

@RunWith(MockitoJUnitRunner.class)
public class SongParserTest {

	@InjectMocks
	private SongParser songParser;

	@Spy
	private ObjectMapper yaml = new ObjectMapper();

	@Spy
	private ChordParser chordParser = new ChordParser(yaml);

	@Spy
	private ChordRepository chordRepository = new ChordRepository(chordParser);

	@Spy
	private ChordLineParser chordLineParser = new ChordLineParser();

	@Spy
	private LyricsAndChordsMerger lyricsAndChordsMerger = new LyricsAndChordsMerger();

	private Set<String> allSongChords;

	@Before
	public void setUp() {
		this.allSongChords = new LinkedHashSet<>();
	}

	private class Text {

		private StringBuilder sb = new StringBuilder();

		public Text append(String text) {
			sb.append(text);
			return this;
		}

		public Text addLine(String line) {
			this.append(line).append(System.getProperty("line.separator"));
			return this;
		}

		public String toString() {
			return this.sb.toString();
		}
	}

	@Test
	public void testNormalLine() {
		Text body = new Text();
		body.addLine("% Am      Em       A");
		body.addLine("> This is the text line");

		String result = songParser.parseBody(body.toString(), allSongChords);

		Assertions.assertThat(result).isEqualTo("<div class=\"line\"><div class=\"chords\"><div is=\"chord\" name=\"Am\">Am</div></div>This is <div class=\"chords\"><div is=\"chord\" name=\"Em\">Em</div></div>the text <div class=\"chords\"><div is=\"chord\" name=\"A\">A</div></div>line</div></div>");
	}

}
