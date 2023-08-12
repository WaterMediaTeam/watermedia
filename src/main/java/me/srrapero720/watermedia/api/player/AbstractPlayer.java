package me.srrapero720.watermedia.api.player;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.events.Event;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayer {
    private final List<Event.Listener<? extends Event>> listeners = new ArrayList<>();
    protected URL url;
    public AbstractPlayer() {}

    protected void compat(String url) {
        URL compat = WaterMediaAPI.url_toURL(url);
        if (compat != null) this.url = compat;
    }

    public <T extends Event> void addEventListener(Event.Listener<T> listener) {
        listeners.add(listener);
    }

    public <T extends Event> void removeEventListener(Event.Listener<T> listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Event> void fireEvent(T eventData) {
        for (Event.Listener<? extends Event> listener : listeners) {
            if (listener.getClass().equals(eventData.getClass()) || listener.getClass().isAssignableFrom(eventData.getClass())) {
                ((Event.Listener<T>) listener).onEvent(eventData);
            }
        }
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

    public abstract boolean isStream();

    public abstract void release();

}
