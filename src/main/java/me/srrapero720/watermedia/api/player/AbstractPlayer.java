package me.srrapero720.watermedia.api.player;

import me.srrapero720.watermedia.api.compat.CompatVideoUrl;
import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractPlayer {
    protected String url;
    public AbstractPlayer(String url) {
        this.compat(url);
    }

    private void compat(String url) {
        var compat = CompatVideoUrl.compat(url);
        if (compat != null) this.url = compat;
        else this.url = url;
    }

    public abstract void start();
    public void start(String url) { compat(url); }
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
