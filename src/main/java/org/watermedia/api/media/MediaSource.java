
package org.watermedia.api.media;

public abstract class MediaSource {
    public static final long NO_DURATION = -1;
    public MediaSource() {

    }

    public abstract int width();

    public abstract int height();

    public abstract boolean start();

    public abstract boolean startPaused();

    public abstract boolean resume();

    public abstract boolean pause();

    public abstract boolean pause(boolean paused);

    public abstract boolean stop();

    public abstract boolean togglePlay();

    public abstract boolean seek(long time);

    public abstract boolean seekQuick(long time);

    public abstract boolean foward();

    public abstract boolean rewind();

    public abstract boolean speed(float speed);

    public abstract boolean repeat();

    public abstract boolean repeat(boolean repeat);

    // status
    public abstract boolean usable();

    public abstract boolean loading();

    public abstract boolean buffering();

    public abstract boolean ready();

    public abstract boolean paused();

    public abstract boolean playing();

    public abstract boolean stopped();

    public abstract boolean ended();

    public abstract boolean validSource();

    public abstract boolean liveSource();

    public abstract boolean canSeek();

    public abstract long duration();

    public abstract long time();

    public abstract void release();
    /**
     * Provides the active texture ID of the media
     * Images can calculate the time by their own
     * @return OpenGL texture id
     */
    public abstract int texture();

    public enum State {
        WAITING,
        LOADING,
        BUFFERING,
        PLAYING,
        PAUSED,
        STOPPED,
        ENDED,
        ERROR;

        public static final State[] VALUES = values();

        public static State of(int state) {
            if (state > VALUES.length)
                throw new IllegalArgumentException("You exceeded the allowed state numbers");
            if (state < 0)
                throw new IllegalArgumentException("What the fuck have in your brain to ask a negative state");
            return VALUES[state];
        }
    }
}
