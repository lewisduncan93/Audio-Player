package application;

import java.util.List;

/**
 * An interface for specifying an audio file library.
 *
 * @author Lewis Duncan
 *
 */
public interface Library {

	/**
	 * Add an audio file to the library.
	 *
	 * @param audioFile the audio file.
	 */
	public void add(AudioFile audioFile);

	/**
	 * Remove an audio file from the library.
	 *
	 * @param audioFile the audio file.
	 */
	public void remove(AudioFile audioFile);

	/**
	 * Increment the play count of an audio file in the library.
	 *
	 * @param audioFile the audio file.
	 */
	public void incrementPlayCount(AudioFile audioFile);

	/**
	 * Get all the audio files in the library.
	 *
	 * @return the collection of audio files.
	 */
	public List<AudioFile> getAll();

}