package application;

/**
 * An audio file with path name and metadata.
 *
 * @author Lewis Duncan
 *
 */
public class AudioFile {

	public String name, artist, album, path;
	public int size, duration, playCount;

	public AudioFile() {
		this.path = path;
	}

	public AudioFile(String name, String artist, String album, int size, int duration, int playCount, String path) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.size = size;
		this.duration = duration;
		this.playCount = playCount;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public int getSize() {
		return size;
	}

	public int getDuration() {
		return duration;
	}

	public int getPlayCount() {
		return playCount;
	}

	public String getFullPath() {
		return path;
	}

	@Override
	public String toString() {
		return String.format("%s - %s (%s)", name, artist, path);
	}
}