package nz.riff.builder.bean;

import java.util.List;

public class Song {

	public static class Link {
		public String media;

		public String description;
	}

	public String name;

	public String artist;

	public String description;

	public String level;

	public String status;

	public List<String> tags;
	
	public List<Link> links;

	public List<Chord> chords;
	
	public String html;
}
