package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.media.InfoApi;
import me.lib720.caprica.vlcj.media.MediaRef;
import me.lib720.caprica.vlcj.media.MediaType;
import me.lib720.caprica.vlcj.media.TrackType;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.MediaPlayerEventListener;
import me.lib720.caprica.vlcj.player.base.State;
import me.lib720.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.SimpleBufferFormatCallback;
import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.url.URLFixer;
import me.srrapero720.watermedia.core.VideoLAN;
import me.srrapero720.watermedia.core.tools.annotations.Experimental;
import me.srrapero720.watermedia.core.tools.annotations.Unstable;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings("unused")
public abstract class BasePlayer {
    protected static final Marker IT = MarkerManager.getMarker("MediaPlayer");
    protected static final ClassLoader CL = Thread.currentThread().getContextClassLoader();
    private static final ExecutorService EX = Executors.newScheduledThreadPool(ThreadCore.getThreadsCount() / 2, ThreadCore.basicThreadFactory("WATERMeDIA-bp-Worker"));

    // PLAYER
    protected String url;
    private CallbackMediaPlayerComponent raw;
    private WaterMediaPlayerEventListener listener;
    public CallbackMediaPlayerComponent raw() { return raw; }
    protected final ReentrantLock playerLock = new ReentrantLock();

    // PLAYER THREAD
    protected volatile boolean live = false;
    protected volatile boolean started = false;
    protected final AtomicInteger volume = new AtomicInteger(100);
    protected final PlayerThread playerThread;

    BasePlayer(MediaPlayerFactory factory, PlayerThread thread, RenderCallback renderCallback, SimpleBufferFormatCallback bufferFormatCallback) {
        this(thread);
        this.init(factory, renderCallback, bufferFormatCallback);
    }

    /**
     * This constructor skips raw player creation, instead waits for {@link #init(MediaPlayerFactory, RenderCallback, SimpleBufferFormatCallback)} to create raw player
     * Intended to be used just in case you need to do some special implementations of {@link RenderCallback} or {@link SimpleBufferFormatCallback}
     * @param thread Async executor for any method executed outside main thread
     */
    protected BasePlayer(PlayerThread thread) { this.playerThread = thread; }


    /**
     * Creates raw player and makes this works normally
     * @param factory MediaPlayerFactory to create raw player, can be null
     * @param renderCallback this is executed when buffer loads media info (first time)
     * @param bufferFormatCallback creates a buffer for the frame
     */
    protected void init(MediaPlayerFactory factory, RenderCallback renderCallback, SimpleBufferFormatCallback bufferFormatCallback) {
        if (WaterMediaAPI.vlc_isReady() && raw == null) {
            if (factory == null) factory = WaterMediaAPI.vlc_getFactory();
            this.raw = new CallbackMediaPlayerComponent(factory, false, renderCallback, bufferFormatCallback);
            raw.mediaPlayer().events().addMediaPlayerEventListener(listener = new WaterMediaPlayerEventListener());
        } else {
            LOGGER.error(IT, "Failed to create raw player because VLC is not loaded");
            this.raw = null;
            this.listener = null;
        }
    }

    private boolean rpa(CharSequence url, String[] vlcArgs) {
        if (raw == null) return false;
        try {
            URLFixer.Result fixedURL = WaterMediaAPI.url_fixURL(url.toString());
            if (fixedURL == null) throw new NullPointerException("URL was invalid");

            this.url = fixedURL.url.toString();
            live = fixedURL.assumeStream;
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load player", e);
        }
        return false;
    }

    private <T> void lockAndExecute(Runnable runnable) {
        playerLock.lock();
        try {
            runnable.run();
        } finally {
            playerLock.unlock();
        }
    }

    public void start(CharSequence url) { this.start(url, new String[0]); }

    public void start(CharSequence url, String[] vlcArgs) {
        EX.execute(() -> {
            if (rpa(url, vlcArgs)) raw.mediaPlayer().media().start(this.url, vlcArgs);
            started = true;
        });
    }

    public void startPaused(CharSequence url) { this.startPaused(url, new String[0]); }
    public void startPaused(CharSequence url, String[] vlcArgs) {
        EX.execute(() -> {
            if (rpa(url, vlcArgs)) raw.mediaPlayer().media().startPaused(this.url, vlcArgs);
            started = true;
        });
    }

    @Unstable
    public State getRawPlayerState() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return State.ERROR;
            return raw.mediaPlayer().status().state();
        });
    }

    public void play() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().play();
        });
    }

    public void pause() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().pause();
        });
    }

    public void togglePlayback() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            if (isPaused()) {
                raw.mediaPlayer().controls().play();
            } else if (isPlaying()) {
                raw.mediaPlayer().controls().pause();
            }
        });
    }

    public void setPauseMode(boolean pauseMode) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().setPause(pauseMode);
        });
    }

    public void stop() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().stop();
        });
    }

    public boolean isSafeUse() { return !playerLock.isLocked(); }
    public boolean isBuffering() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().state().equals(State.BUFFERING);
        });
    }
    public boolean isReady() {
        return ThreadCore.lockExecute(playerLock, () -> {
           if (raw == null) return false;
           return raw.mediaPlayer().status().isPlayable();
        });
    }
    public boolean isPaused() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().state().equals(State.PAUSED);
        });
    }
    public boolean isStopped() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().state().equals(State.STOPPED);
        });
    }
    public boolean isEnded() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().state().equals(State.ENDED);
        });
    }
    public boolean isBroken() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return true;
            return raw.mediaPlayer().status().state().equals(me.lib720.caprica.vlcj.player.base.State.ERROR);
        });
    }

    public boolean isValid() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().media().isValid();
        });
    }

    public boolean isPlaying() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().isPlaying();
        });
    }

    /**
     * Method is currently incomplete
     * it cannot distingue if was a stream after get media information
     * that is supplied with our API but isn't enough, because other type of streams cannot be handled
     * @return if mrl was a livestream
     */
    @Experimental
    public boolean isLive() {
        if (live) return true;

        if (url.endsWith(".m3u8")) {
            if (getMediaInfoDuration() == -1) return true;
            if (getTime() > getDuration()) return true;
        }

        return false;
    }

    /**
     * This metrod is gonna begin removed on version 2.1.0
     * @deprecated
     * @return is was a stream based on m3u8 stream
     */
    @Deprecated
    public boolean isStream() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            InfoApi mediaInfo = raw.mediaPlayer().media().info();
            return mediaInfo != null && (mediaInfo.type().equals(MediaType.STREAM) || mediaInfo.mrl().endsWith(".m3u") || mediaInfo.mrl().endsWith(".m3u8"));
        });
    }

    public boolean isSeekAble() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().status().isSeekable();
        });
    }

    public void seekTo(long time) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().setTime(time);
        });
    }

    public void seekFastTo(long ticks) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().setTime(ticks);
        });
    }

    /**
     * Use {@link BasePlayer#seekTo(long)} in conjunction with {@link WaterMediaAPI#math_ticksToMillis(int)}
     * @deprecated is gonna being removed for 2.1.0
     */
    public void seekMineTo(int ticks) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            long time = WaterMediaAPI.math_ticksToMillis(ticks);
            raw.mediaPlayer().controls().setTime(time);
        });
    }

    /**
     * Use {@link BasePlayer#seekFastTo(long)} in conjunction with {@link WaterMediaAPI#math_ticksToMillis(int)}
     * @deprecated is gonna being removed for 2.1.0
     */
    public void seekMineFastTo(int ticks) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().setTime(WaterMediaAPI.math_ticksToMillis(ticks));
        });
    }

    public void foward() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().skipTime(5000L);
        });
    }

    public void rewind() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().skipTime(-5000L);
        });
    }

    public void setSpeed(float rate) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().setRate(rate);
        });
    }

    public void setVolume(int volume) {
        this.volume.set(volume);
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().audio().setVolume(this.volume.get());
            if (this.volume.get() == 0 && !raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(true);
            else if (this.volume.get() > 0 && raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(false);
        });
    }

    public int getVolume() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return volume.get();
            return raw.mediaPlayer().audio().volume();
        });
    }

    public void mute() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().audio().setMute(true);
        });
    }

    public void unmute() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().audio().setMute(false);
        });
    }

    public void setMuteMode(boolean mode) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().audio().setMute(mode);
        });
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    public long getDuration() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return 0L;
            if (!isValid() || (RuntimeUtil.isNix() && getRawPlayerState().equals(State.STOPPED))) return 0L;
            return raw.mediaPlayer().status().length();
        });
    }

    /**
     * Use {@link BasePlayer#getDuration()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
     * @deprecated is gonna being removed for 2.1.0
     */
    @Deprecated
    public int getMineDuration() {
        if (raw == null) return 0;
        synchronized (this) { return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().length()); }
    }

    public long getMediaInfoDuration() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return 0L;
            InfoApi info = raw.mediaPlayer().media().info();
            if (info != null) return info.duration();
            return 0L;
        });
    }

    /**
     * Use {@link BasePlayer#getMediaInfoDuration()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
     * @deprecated is gonna being removed for 2.1.0
     */
    @Deprecated
    public int getMineMediaInfoDuration() {
        if (raw == null) return 0;
        synchronized (this) {
            InfoApi info = raw.mediaPlayer().media().info();
            if (info != null) return WaterMediaAPI.math_millisToTicks(info.duration());
            return 0;
        }
    }

    public long getTime() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return 0L;
            return raw.mediaPlayer().status().time();
        });
    }

    /**
     * Use {@link BasePlayer#getTime()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
     * @deprecated is gonna being removed for 2.1.0
     */
    @Deprecated
    public int getMineTime() {
        if (raw == null) return 0;
        synchronized (this) { return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().time()); }
    }

    public boolean getRepeatMode() {
        return ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return false;
            return raw.mediaPlayer().controls().getRepeat();
        });
    }
    public void setRepeatMode(boolean repeatMode) {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().controls().setRepeat(repeatMode);
        });
    }

    public void release() {
        ThreadCore.lockExecute(playerLock, () -> {
            if (raw == null) return;
            raw.mediaPlayer().release();
            raw = null;

            // I AM DISSAPOINTMENT WITH THIS
            // gc deletes callback when vlc (for some no reason) still requires callback after begin released
            // with this code I ensure is still referenced for at least 3 seconds, then clear any reference, so MediaPlayer and Callback can DIE
            AtomicReference<WaterMediaPlayerEventListener> ls = new AtomicReference<>(listener);
            ThreadCore.sleep(3000);
            listener = null;
            ls.set(null);
        });
    }

    public void releaseAsync() {
        if (raw == null) return;
        EX.execute(this::release);
    }

    protected static void checkClassLoader() {
        if (Thread.currentThread().getContextClassLoader() == null) Thread.currentThread().setContextClassLoader(CL);
    }

    @SuppressWarnings("ConstantConditions")
    private final class WaterMediaPlayerEventListener implements MediaPlayerEventListener {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
            checkClassLoader();
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float newCache) {
            checkClassLoader();
            if (newCache >= 100) setVolume(volume.get());
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            checkClassLoader();
            playerThread.askForExecution(() -> {
                setVolume(volume.get());
            });
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void forward(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void backward(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            checkClassLoader();
        }

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
            checkClassLoader();
        }

        @Override
        public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
            checkClassLoader();
        }

        @Override
        public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
            checkClassLoader();
        }

        @Override
        public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
            checkClassLoader();
        }

        @Override
        public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
            checkClassLoader();
        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
            checkClassLoader();
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
            checkClassLoader();
        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
            checkClassLoader();
        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkClassLoader();
        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkClassLoader();
        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkClassLoader();
        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean corked) {
            checkClassLoader();
        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean muted) {
            checkClassLoader();
        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
            checkClassLoader();
        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
            checkClassLoader();
        }

        @Override
        public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
            checkClassLoader();
        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            checkClassLoader();
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            checkClassLoader();
            playerThread.askForExecution(() -> {
                setVolume(volume.get());
            });
        }
    }

    public interface PlayerThread { void askForExecution(Runnable runnable); }
}