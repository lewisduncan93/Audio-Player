package application;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Player {

	private AudioFile currentAudioFile;
	private MediaPlayer mediaPlayer;
	private ChangeListener<Duration> durationChangeListener;
	private boolean playing;

	public Player() {

	}

	public void playAudioFile(AudioFile audioFile) {
		if (currentAudioFile != audioFile) {
			if (playing)
				mediaPlayer.stop();
			currentAudioFile = audioFile;
			String uri = new File(audioFile.getFullPath()).toURI().toASCIIString();
			mediaPlayer = new MediaPlayer(new Media(uri));



			if (durationChangeListener != null) {
				mediaPlayer.currentTimeProperty().addListener(durationChangeListener);
			}

		}
		mediaPlayer.play();
		playing = true;
	}

	public void playCurrentAudioFile() {
		mediaPlayer.play();
		playing = true;
	}

	public void pauseAudioFile() {
		mediaPlayer.pause();
		playing = false;
	}

	public void playPrevAudioFile() {

	}

	public void playNextAudioFile() {

	}

	public void seek(double time) {
		mediaPlayer.seek(new Duration(time));
	}

	public double getTotalDuration() {
		return mediaPlayer.getTotalDuration().toMillis();
	}

	public void setVolume(float volume) {
		mediaPlayer.setVolume(volume);
	}

	public void addDurationChangeListener(ChangeListener<Duration> listener) {
		durationChangeListener = listener;
	}

	public void setMetaData(AudioFile audioFile){
		if (currentAudioFile == audioFile){

			audioFile.getArtist();
			audioFile.getAlbum();
			audioFile.getSize();
			audioFile.getDuration();
		}
	}


	public boolean isPlaying() {
		return playing;
	}
}
