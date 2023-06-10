package me.srrapero720.watermedia.api.media.players;

import org.apache.commons.lang3.NotImplementedException;

public class LavaPlayer extends Player {
    public LavaPlayer(String url) {
        super(url);
    }

    @Override
    public void start() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void play() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void pause() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void setPauseMode(boolean isPaused) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void stop() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void seekTo(long time) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void seekFastTo(long ticks) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void seekGameTicksTo(int ticks) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void seekGameTickFastTo(int ticks) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getGameTickDuration() {
        return 0;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public long getGameTickTime() {
        return 0;
    }

    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public void setRepeatMode(boolean repeatMode) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public boolean getRepeatMode() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void setSpeed(float rate) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void fastFoward() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void rewind() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public void setVolume(int volume) {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void release() {
        throw new NotImplementedException("LavaPlayer isn't implemented yet");
    }
}
