package me.srrapero720.watermedia.api.player.impl;

import me.srrapero720.watermedia.api.player.IMediaPlayer;

import java.io.FileInputStream;
import java.io.InputStream;

public class FFmpegPlayer implements IMediaPlayer {

    public FFmpegPlayer() {

    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean startPaused() {
        return false;
    }

    @Override
    public boolean resume() {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean pause(boolean paused) {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public boolean togglePlay() {
        return false;
    }

    @Override
    public boolean seek(long time) {
        return false;
    }

    @Override
    public boolean seekQuick(long time) {
        return false;
    }

    @Override
    public boolean fastFoward() {
        return false;
    }

    @Override
    public boolean fastRewind() {
        return false;
    }

    @Override
    public boolean speed(float speed) {
        return false;
    }

    @Override
    public boolean mute() {
        return false;
    }

    @Override
    public boolean mute(boolean muted) {
        return false;
    }

    @Override
    public boolean unmute() {
        return false;
    }

    @Override
    public boolean repeaat() {
        return false;
    }

    @Override
    public boolean repeaat(boolean repeat) {
        return false;
    }

    @Override
    public boolean usable() {
        return false;
    }

    @Override
    public boolean loading() {
        return false;
    }

    @Override
    public boolean buffering() {
        return false;
    }

    @Override
    public boolean ready() {
        return false;
    }

    @Override
    public boolean paused() {
        return false;
    }

    @Override
    public boolean playing() {
        return false;
    }

    @Override
    public boolean stopped() {
        return false;
    }

    @Override
    public boolean ended() {
        return false;
    }

    @Override
    public boolean muted() {
        return false;
    }

    @Override
    public boolean validSource() {
        return false;
    }

    @Override
    public boolean liveSource() {
        return false;
    }

    @Override
    public boolean canSeek() {
        return false;
    }

    @Override
    public long duration() {
        return 0;
    }

    @Override
    public long time() {
        return 0;
    }

    @Override
    public void release() {

    }
}
