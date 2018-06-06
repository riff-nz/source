package nz.riff.writer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cleaner {

	private static final Logger log = LoggerFactory.getLogger(Cleaner.class);

	public void clean(String dir) {
		try {
			FileUtils.deleteDirectory(new File(dir));
		} catch (IOException e) {
			log.error("Problem when cleaning up the dir {}", dir);
		}
	}
}
