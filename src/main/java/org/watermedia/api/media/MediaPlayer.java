
package org.watermedia.api.media;

import org.watermedia.api.network.MRL;
import org.watermedia.tools.ThreadTool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class MediaPlayer {
    protected static final Executor POOL = Executors.newScheduledThreadPool(ThreadTool.minThreads());
    public static final long NO_DURATION = -1;
    public static final long NO_TIME = -1;
    public static final int NO_SIZE = -1;

    protected final MRL mrl;
    protected final Type type;
    protected final Quality quality;
    protected final Executor mainExecutor;
    public MediaPlayer(MRL mrl, Type type, Quality quality, Executor mainExecutor) {
        this.mrl = mrl;
        this.type = type;
        this.quality = quality;
        this.mainExecutor = mainExecutor;
    }

    public MediaPlayer() {
        this.mrl = null;
        this.type = Type.UNKNOWN;
        this.quality = Quality.AVERAGE; // default quality
        this.mainExecutor = null; // default executor
    }

    public int width() {
        return NO_SIZE;
    }

    public int height() {
        return NO_SIZE;
    }

    public abstract void start();

    public abstract void startPaused();

    public abstract boolean startSync();

    public abstract boolean startSyncPaused();

    public abstract boolean resume();

    public abstract boolean pause();

    public abstract boolean pause(boolean paused);

    public abstract boolean stop();

    public abstract boolean togglePlay();

    public abstract boolean seek(long time);

    public abstract boolean seekQuick(long time);

    public abstract boolean foward();

    public abstract boolean rewind();

    public abstract float speed();

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

    public abstract void volume(float volume);

    public abstract float volume();

    public abstract void mute(boolean mute);

    public abstract boolean mute();

    public abstract void quality(Quality quality);

    public abstract Quality quality();

    public abstract Type type();

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

    /**
     * Quality preference.
     */
    public enum Quality {
        /**
         * Qualities same or below 240p threshold
         */
        LOWEST(240),

        /**
         * Qualities same or below 480p threshold
         */
        LOWER(480),

        /**
         * Qualities below 540p threshold
         */
        LOW(540),

        /**
         * Qualities same or below 720p threshold
         */
        AVERAGE(720),

        /**
         * Qualities same or below 1080p threshold
         */
        HIGH(1080),

        /**
         * Qualities same or below 2K threshold
         */
        HIGHER(1440),

        /**
         * Qualities same or below 4K threshold
         */
        HIGHEST(2160);

        private final int threadshool;
        Quality(int threshold) {
            this.threadshool = threshold;
        }

        public static final Quality[] VALUES = values();

        public static Quality calculate(int width) { // TODO: evaluate height for tiktok reels
            if (width >= LOWEST.threadshool  && width < LOWER.threadshool) {
                return LOWEST;
            } else if (width >= LOWER.threadshool && width < LOW.threadshool) {
                return LOWER;
            } else if (width >= LOW.threadshool && width < AVERAGE.threadshool) {
                return LOW;
            } else if (width >= AVERAGE.threadshool && width < HIGH.threadshool) {
                return AVERAGE;
            } else if (width >= HIGH.threadshool && width < HIGHER.threadshool) {
                return HIGH;
            } else if (width >= HIGHER.threadshool && width < HIGHEST.threadshool) {
                return HIGHER;
            } else {
                return HIGHEST;
            }
        }

        public Quality getNext() {
            var ordinal = this.ordinal() + 1;
            if (ordinal >= VALUES.length) {
                return null;
            }
            return VALUES[ordinal];
        }

        public Quality getBack() {
            var ordinal = this.ordinal() - 1;
            if (ordinal <= 0) {
                return null;
            }
            return VALUES[ordinal];
        }
    }

    public enum Type {
        IMAGE,
        AUDIO,
        VIDEO,
        SUBTITLES,
        UNKNOWN;

        public static Type getByMimetype(String mimetype) {
            String[] mm = mimetype.split("/");
            String type = mm[0];
            String format = mm.length == 1 ? null : mm[1].toLowerCase();

            return switch (type) {
                case "video" -> VIDEO;
                case "audio" -> AUDIO;
                case "text" -> format != null && (format.equals("str") || format.equals("plain")) ? SUBTITLES : UNKNOWN;
                default -> UNKNOWN;
            };
        }
    }
}
