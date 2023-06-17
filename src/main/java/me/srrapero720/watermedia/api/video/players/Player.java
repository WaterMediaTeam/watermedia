package me.srrapero720.watermedia.api.video.players;

import me.srrapero720.watermedia.api.MediaApiCore;
import me.srrapero720.watermedia.api.video.players.events.EventManager;
import me.srrapero720.watermedia.api.video.players.events.common.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected String url;
    public Player() {}

    protected void compat(String url) {
        var compat = MediaApiCore.patchNonStaticUrl(url);
        if (compat != null) this.url = compat;
        else this.url = url;
    }

    public void start(@NotNull CharSequence url) { compat(url.toString()); }
    public void prepare(@NotNull CharSequence url) { compat(url.toString()); }
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

    public abstract void setSpeed(float rate);
    public abstract void fastFoward();
    public abstract void rewind();
    public synchronized void fastBackward() { rewind(); }

    public abstract void setVolume(int volume);
    public abstract int getVolume();

    public abstract void release();
}
