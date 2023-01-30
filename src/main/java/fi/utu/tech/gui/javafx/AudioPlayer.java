package fi.utu.tech.gui.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Luokka lataa pelissä esiintyvät äänitiedostot ja tarjoaa metodit niiden soittamiseen
 */

public final class AudioPlayer {
    private static final URL MUSIC = AudioPlayer.class.getResource("battle-of-the-dragons-8037.mp3");
    private static final URL BATTLEMUSIC = AudioPlayer.class.getResource("rhythm-of-war-main-7233.mp3");
    private static final URL MISS = AudioPlayer.class.getResource("splash.wav");
    private static final URL HIT = AudioPlayer.class.getResource("explosion.wav");
    private static final URL SUNK = AudioPlayer.class.getResource("bubble.wav");
    private static final URL WIN = AudioPlayer.class.getResource("winsound.mp3");
    private static final URL CLICK = AudioPlayer.class.getResource("buttonclick.mp3");
    private static MediaPlayer MUSICPLAYER;
    private static MediaPlayer MISSPLAYER;
    private static MediaPlayer HITPLAYER;
    private static MediaPlayer SUNKPLAYER;
    private static MediaPlayer WINPLAYER;
    private static MediaPlayer CLICKPLAYER;

    // Setup musiikki
    public static void playMusic() {
    	if (MUSICPLAYER != null) {
    		MUSICPLAYER.stop();
    	}
    	MUSICPLAYER = new MediaPlayer(new Media(MUSIC.toString()));
        MUSICPLAYER.setOnEndOfMedia(() -> MUSICPLAYER.seek(Duration.ZERO));
        MUSICPLAYER.setVolume(0.3);
        MUSICPLAYER.play();
    }

    // Taistelun aikainen musiikki
    public static void playBattleMusic() {
    	boolean mute = false;
    	if(MUSICPLAYER.isMute()) {
    		mute = true;
    	}
    	MUSICPLAYER.stop();
    	MUSICPLAYER = new MediaPlayer(new Media(BATTLEMUSIC.toString()));
    	MUSICPLAYER.setOnEndOfMedia(() -> MUSICPLAYER.seek(Duration.ZERO));
    	if (mute) {
    		MUSICPLAYER.setVolume(0);
    	}
    	else {
    		MUSICPLAYER.setVolume(0.3);
    	}
    	MUSICPLAYER.play();
    }

    public static void muteMusic() {
        if (MUSICPLAYER.getVolume() == 0) {
            MUSICPLAYER.setMute(false);
            MUSICPLAYER.setVolume(0.3);
        } else {
            MUSICPLAYER.setMute(true);
            MUSICPLAYER.setVolume(0);
        }
    }

    public static BooleanProperty getMusicMuteProperty() {
        return MUSICPLAYER.muteProperty();
    }

    // Tyhjään ruutuun ammmuttaessa
    public static void playMissSfx() {
        MISSPLAYER = new MediaPlayer(new Media(MISS.toString()));
        MISSPLAYER.play();
    }

    // Laivaan osuttaessa
    public static void playHitSfx() {
        HITPLAYER = new MediaPlayer(new Media(HIT.toString()));
        HITPLAYER.play();
    }

    // Laivan upotessa
    public static void playSunkSfx() {
        SUNKPLAYER = new MediaPlayer(new Media(SUNK.toString()));
        SUNKPLAYER.play();
    }
    
    // Voittoääni
    public static void playWinSfx() {
    	MUSICPLAYER.stop();
    	WINPLAYER = new MediaPlayer(new Media(WIN.toString()));
    	WINPLAYER.play();
    }
    
    // Napin painallus ääni
    public static void playClickSfx() {
    	CLICKPLAYER = new MediaPlayer(new Media(CLICK.toString()));
    	CLICKPLAYER.setVolume(0.5);
    	CLICKPLAYER.play();
    }
}
