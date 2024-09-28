package me.srrapero720.watermedia.api.player;

public interface IMediaPlayer {
    boolean start();

    boolean startPaused();

    boolean resume();

    boolean pause();

    boolean pause(boolean paused);

    boolean stop();

    boolean togglePlay();

    boolean seek(long time);

    boolean seekQuick(long time);

    boolean fastFoward();

    boolean fastRewind();

    boolean speed(float speed);

    boolean mute();

    boolean mute(boolean muted);

    boolean unmute();

    boolean repeaat();

    boolean repeaat(boolean repeat);

    // status
    boolean usable();

    boolean loading();

    boolean buffering();

    boolean ready();

    boolean paused();

    boolean playing();

    boolean stopped();

    boolean ended();

    boolean muted();

    boolean validSource();

    boolean liveSource();

    boolean canSeek();

    long duration();

    long time();

    void release();

    enum Status {
    }
}
