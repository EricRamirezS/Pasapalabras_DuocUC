package Controladores;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Pasapalabras_DuocUC
 * <p>
 * Created by Eric Ramírez Santis on 04-09-2018.
 * Github Account: https://github.com/EricRamirezS
 */
class Audio {
	private Media BGM;
	private Media SFX;
	private MediaPlayer BGMPlayer;
	private MediaPlayer SFXPlayer;
	private Double BGMVol = 1D;
	private Double SFXVol = 1D;
	private String Path;

	/**
	 * Constuctor, plays a random audio for MainMenu
	 */
	Audio() {
	}

	/**
	 * Plays a Background Music
	 *
	 * @param BGMname File Name
	 */
	void playBGM(String BGMname) {
		if (BGM != null) {
			BGMPlayer.stop();
		}
		Path = (new File("audio/" + BGMname + ".mp3")).toURI().toString();
		BGM = new Media(Path);
		BGMPlayer = new MediaPlayer(BGM);
		setVolume();
		BGMPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		BGMPlayer.setAutoPlay(true);
	}

	void pauseBGM() {
		if (BGMPlayer != null) {
			BGMPlayer.pause();
		}
	}

	void resumeBGM() {
		if (BGMPlayer != null) {
			BGMPlayer.play();
		}
	}

	/**
	 * Stop a BGM
	 */
	void stopBGM() {
		BGMPlayer.stop();
	}

	/**
	 * Plays a SFX
	 *
	 * @param SFXname File Name
	 */
	void playAudio(String SFXname) {
		Path = (new File("audio/" + SFXname + ".mp3")).toURI().toString();
		SFX = new Media(Path);
		SFXPlayer = new MediaPlayer(SFX);
		setVolume();
		SFXPlayer.setAutoPlay(true);
	}

	/**
	 * Updates volume
	 */
	private void setVolume() {
		if (BGMPlayer != null)
			BGMPlayer.setVolume(BGMVol);
		if (SFXPlayer != null)
			SFXPlayer.setVolume(SFXVol);
	}
}