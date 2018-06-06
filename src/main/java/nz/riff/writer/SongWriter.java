package nz.riff.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.riff.builder.bean.Song;
import nz.riff.builder.bean.SongIndex;

public class SongWriter {

	private static final Logger log = LoggerFactory.getLogger(SongWriter.class);

	private ObjectMapper mapper;

	@Inject
	public SongWriter(@Named("json") ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}

	public void write(String outputDir, Map<String, Song> songs) {
		List<SongIndex> index = new ArrayList<>();
		for (Entry<String, Song> entry : songs.entrySet()) {
			SongIndex songIndex = new SongIndex();
			this.write(outputDir + "songs/" + entry.getKey(), entry.getValue());

			songIndex.name = entry.getValue().name;
			songIndex.artist = entry.getValue().artist;
			songIndex.path = entry.getKey();
			index.add(songIndex);
		}
		try {
			mapper.writeValue(new File(outputDir + "songs/index.json"), index);
		} catch (Exception e) {
			log.error("Problen when writing song index", e);
		}
	}

	public void write(String path, Song song) {
		try {
			File directory = new File(path.substring(0, path.lastIndexOf("/")));
			directory.mkdirs();
			mapper.writeValue(new File(path + ".json"), song);
		} catch (Exception e) {
			log.error("Problen when writing song", e);
		}
	}
}
