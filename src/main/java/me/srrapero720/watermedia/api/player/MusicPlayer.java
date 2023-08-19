package me.srrapero720.watermedia.api.player;

import com.sun.jna.Pointer;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.callback.AudioCallback;
import me.srrapero720.watermedia.core.tools.annotations.Experimental;

import java.util.ArrayList;
import java.util.Arrays;

@Experimental
public class MusicPlayer extends MediaPlayerBase {
    WaterAudioCallback callback;
    public MusicPlayer(MediaPlayerFactory factory, PlayerThread playerThread, WaterAudioCallback callback) {
        super(factory, playerThread, null, null);
        this.callback = callback;
        if (callback != null) this.raw.mediaPlayer().audio().callback("ogg", 44100, 2, callback);
    }

    @Override public synchronized void start(CharSequence url) { this.start(url, new String[0]); }
    @Override public synchronized void start(CharSequence url, String[] vlcArgs) {
        if (callback != null) {
            ArrayList<String> extra = (ArrayList<String>) Arrays.asList(vlcArgs);
            extra.add(":aout");
            extra.add("amem");

            super.start(url, extra.toArray(new String[0]));
        } else {
            super.start(url, vlcArgs);
        }
    }

    @Override public synchronized void startPaused(CharSequence url) { this.startPaused(url, new String[0]); }
    @Override public synchronized void startPaused(CharSequence url, String[] vlcArgs) {
        if (callback != null) {
            ArrayList<String> extra = (ArrayList<String>) Arrays.asList(vlcArgs);
            extra.add(":aout");
            extra.add("amem");

            super.startPaused(url, extra.toArray(new String[0]));
        } else {
            super.startPaused(url, vlcArgs);
        }
    }

    public static abstract class WaterAudioCallback implements AudioCallback {
        @Override
        public void play(MediaPlayer mediaPlayer, Pointer samples, int sampleCount, long pts) {
            checkClassLoader();
        }

        @Override
        public void pause(MediaPlayer mediaPlayer, long pts) {
            checkClassLoader();
        }

        @Override
        public void resume(MediaPlayer mediaPlayer, long pts) {
            checkClassLoader();
        }

        @Override
        public void flush(MediaPlayer mediaPlayer, long pts) {
            checkClassLoader();
        }

        @Override
        public void drain(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void setVolume(float volume, boolean mute) {
            checkClassLoader();
        }
    }
}
