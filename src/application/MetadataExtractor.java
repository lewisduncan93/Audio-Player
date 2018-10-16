package application;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;

/**
 * An extraction of the metadata for each audiofile.
 *
 * @author Lewis Duncan
 *
 */
public class MetadataExtractor {

	public static AudioFile createAudioFileFromFile(File file) throws Exception {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream is = new FileInputStream(file);
		ParseContext pc = new ParseContext();

		Mp3Parser mp3Parser = new Mp3Parser();
		mp3Parser.parse(is, handler, metadata, pc);

		System.out.println("Contents of the document:" + handler.toString());
		System.out.println("Metadata of the document:");
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
			System.out.println(name + ": " + metadata.get(name));
		}

		String name = metadata.get("title");
		String artist = metadata.get("creator");
		String album = metadata.get("xmpDM:album");
		int size = 0;
		int duration = Integer.parseInt(metadata.get("xmpDM:duration").split("\\.")[0]);

		return new AudioFile(name, artist, album, size, duration, 0, file.getAbsolutePath());
	}

}
