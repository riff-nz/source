package nz.riff.builder.parsers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LyricsAndChordsMerger {
	public String merge(String lyrics, Map<Integer, List<String>> chords) {
		StringBuilder merger = new StringBuilder();

		int lastPosition = 0;
		if (chords != null && chords.size() > 0) {
			lastPosition = (int) chords.keySet().toArray()[chords.size() - 1];
		}

		if (lyrics == null) {
			lyrics = "";
		}

		if (lastPosition > lyrics.length()) {
			lyrics += String.join("", Collections.nCopies(lastPosition - lyrics.length(), " "));
		}

		for (int i = 0; i <= lyrics.length(); i++) {
			if (chords.containsKey(i)) {
				merger.append(this.wrapChords(chords.get(i)));
			}

			if (lyrics.length() > i) {
				merger.append(lyrics.charAt(i));
			}
		}
		return merger.toString();
	}

	String wrapChords(List<String> chords) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"chords\">");
		for (String chord : chords) {
			sb.append("<div class=\"chord\" is=\"chord\" name=\"" + chord + "\">" + chord + "</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}
}
