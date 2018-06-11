package nz.riff.builder.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.riff.builder.bean.Song;
import nz.riff.builder.parsers.SongParser;

public class SongRepository {

	private static final Logger log = LoggerFactory.getLogger(SongRepository.class);

	private SongParser songParser;

	@Inject
	public SongRepository(SongParser songParser) {
		super();
		this.songParser = songParser;
	}

	public Map<String, Song> get() {
		Map<String, Song> songs = new LinkedHashMap<>();

		List<File> files = new ArrayList<File>(FileUtils.listFiles(new File("./data/songs/"), new SuffixFileFilter(".song"), TrueFileFilter.INSTANCE));
		Collections.sort(files, new PathFileComparator());
		
		for (File file : files) {
			try {
				log.debug("Parsing song \"{}\"", file.getParentFile().getName() + "/" + file.getName());
				Song song = this.songParser.parse(FileUtils.readFileToString(file, "UTF-8"));
				songs.put(this.getPath(file), song);
				log.debug("Parsed song \"{}\" ({} chords)", file.getParentFile().getName() + "/" + file.getName(), song.chords.size());
			} catch (Exception e) {
				log.error("Problem when parsing song {}", file, e);
			}
		}

		return songs;
	}

	String getPath(File file) {
		return file.getParentFile().getName() + "/" + FilenameUtils.getBaseName(file.getName());
	}
}
