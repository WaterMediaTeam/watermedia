package me.srrapero720.watermedia.api.player;

import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractPlayer {
    protected final String url;
    public AbstractPlayer(String url) { this.url = url; }
    public abstract void start();
    public abstract void play();
    public abstract void pause();
    public abstract void setPauseMode(boolean isPaused);
    public abstract void stop();


    /* seekTo */
    public abstract void seekTo(long time);
    public abstract void seekFastTo(int ticks);
    public abstract void seekGameTicksTo(int ticks);
    public abstract void seekGameTickFastTo(int ticks);

    public abstract long getDuration();
    public abstract long getGameTickDuration();

    public abstract void setRepeatMode(boolean repeatMode);
    public abstract boolean getRepeatMode();

    public abstract void setSpeed(float rate);
    public abstract void fastFoward();
    public abstract void rewind();
    public synchronized void fastBackward() { rewind(); }

    public abstract void setVolume(@IntRange(from = 0, to = 100) int volume);
    public abstract int getVolume();

    public abstract void release();
}
