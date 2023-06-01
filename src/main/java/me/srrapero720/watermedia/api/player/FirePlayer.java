package me.srrapero720.watermedia.api.player;

import org.checkerframework.common.value.qual.IntRange;

public class FirePlayer extends AbstractPlayer {
    public FirePlayer(String url) {
        super(url);
    }

    @Override
    public void start() {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void setPauseMode(boolean isPaused) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void seekTo(long time) {

    }

    @Override
    public void seekFastTo(int ticks) {

    }

    @Override
    public void seekGameTicksTo(int ticks) {

    }

    @Override
    public void seekGameTickFastTo(int ticks) {

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
    public void setRepeatMode(boolean repeatMode) {

    }

    @Override
    public boolean getRepeatMode() {
        return false;
    }

    @Override
    public void setSpeed(float rate) {

    }

    @Override
    public void fastFoward() {

    }

    @Override
    public void rewind() {

    }

    @Override
    public void setVolume(@IntRange(from = 0, to = 100) int volume) {

    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void release() {

    }
}
