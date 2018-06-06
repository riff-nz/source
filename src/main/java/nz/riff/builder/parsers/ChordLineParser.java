package nz.riff.builder.parsers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChordLineParser {
	public Map<Integer, List<String>> parse(String line) {
		Map<Integer, List<String>> chords = new LinkedHashMap<>();
		line = line + " ";

		int numberOfSpaces = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == ' ') {
				numberOfSpaces++;
				continue;
			}

			String chord = line.substring(i, i + line.substring(i).indexOf(" "));
			int position = (chords.size() > 0 && numberOfSpaces == 1) ? (int) chords.keySet().toArray()[chords.size() - 1] : i;
			i = i + chord.length() - 1;
			numberOfSpaces = 0;
			
			if (chord.startsWith("(")) {
				continue;
			}

			if (!chords.containsKey(position)) {
				chords.put(position, new ArrayList<String>());
			}
			chords.get(position).add(chord);
		}

		return chords;
	}
}
