package me.srrapero720.watermedia.api.audio;

import me.srrapero720.watermedia.api.WaterMediaAPI;

import java.awt.*;

public abstract class AudioPlayer {
    protected String url;
    public AudioPlayer() {}

    protected void compat(String url) {
        String compat = WaterMediaAPI.urlPatch(url);
        if (compat != null) this.url = compat;
        else this.url = url;
    }

    public void start(CharSequence url) { compat(url.toString()); }
    public void prepare(CharSequence url) { compat(url.toString()); }
    public abstract void play();
    public abstract void pause();
    public abstract void setPauseMode(boolean isPaused);
    public abstract void stop();


    /* seekTo */
    public abstract void seekTo(long time);
    public abstract void seekFastTo(long ticks);
    public abstract void seekGameTicksTo(int ticks);
    public abstract void seekGameTickFastTo(int ticks);

    public abstract long getDuration();
    public abstract int getGameTickDuration();

    public abstract long getTime();

    public abstract int getGameTickTime();

    public abstract boolean isSeekable();
    public abstract void setRepeatMode(boolean repeatMode);
    public abstract boolean getRepeatMode();

    public abstract boolean isPlaying();
    public abstract boolean isValid();

    public abstract Dimension getDimensions();

    public abstract void setSpeed(float rate);
    public abstract void fastFoward();
    public abstract void rewind();
    public synchronized void fastBackward() { rewind(); }

    public abstract void setVolume(int volume);
    public abstract int getVolume();

    public abstract void mute();

    public abstract void unmute();

    public abstract void setMuteMode(boolean mode);

    public abstract boolean isPodcast();

    public abstract void release();

}
