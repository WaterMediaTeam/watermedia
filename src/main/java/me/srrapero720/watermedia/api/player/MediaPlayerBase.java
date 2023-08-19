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
import me.srrapero720.watermedia.api.player.events.*;
import me.srrapero720.watermedia.api.url.FixerBase;
import me.srrapero720.watermedia.core.tools.annotations.Experimental;
import me.srrapero720.watermedia.core.tools.annotations.Untested;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public abstract class MediaPlayerBase extends EventManager {
    protected static final ClassLoader LOADER = Thread.currentThread().getContextClassLoader();
    protected static final Marker IT = MarkerManager.getMarker("MediaPlayer");
    private static final AtomicInteger WK_TH = new AtomicInteger(0);
    private static final ExecutorService EX = Executors.newScheduledThreadPool(ThreadCore.getMinThreadCount(), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setPriority(7);
        t.setName("WM-MediaPlayer-Worker-" + WK_TH.incrementAndGet());
        return t;
    });


    protected String url;

    // PLAYER
    public final CallbackMediaPlayerComponent raw;
    protected final AtomicBoolean started = new AtomicBoolean(false);
    protected final AtomicBoolean buffering = new AtomicBoolean(false);
    protected final AtomicBoolean prepared = new AtomicBoolean(false);
    protected final AtomicInteger volume = new AtomicInteger(100);
    protected final AtomicBoolean assumeStream = new AtomicBoolean(false);

    public MediaPlayerBase(MediaPlayerFactory factory, RenderCallback renderCallback, SimpleBufferFormatCallback bufferFormatCallback) {
        if (WaterMediaAPI.vlc_isReady()) {
            this.raw = new CallbackMediaPlayerComponent(factory, false, renderCallback, bufferFormatCallback);
            raw.mediaPlayer().events().addMediaPlayerEventListener(new WaterMediaPlayerEventListener());
        } else {
            LOGGER.error(IT, "Failed to create raw player because VLC is not loaded");
            this.raw = null;
        }
    }

    public void start(CharSequence url) { this.start(url, new String[0]); }
    public void start(CharSequence url, String[] vlcArgs) {
        if (raw == null) return;
        EX.execute(() -> {
            try {
                if (isPlaying()) stop();
                FixerBase.Result result = WaterMediaAPI.url_fixURL(url.toString());

                if (result != null) {
                    this.url = result.url.toString();
                    assumeStream.set(result.assumeStream);

                    raw.mediaPlayer().media().start(this.url, vlcArgs);
                    started.set(true);
                } else LOGGER.error(IT, "Playback start failed. URL is invalid or null");
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to start player", e);
            }
        });
    }
    public void prepare(CharSequence url) { this.prepare(url, new String[0]); }
    public void prepare(CharSequence url, String[] vlcArgs) {
        if (raw == null) return;
        EX.execute(() -> {
            try {
                if (isPlaying()) stop();
                FixerBase.Result result = WaterMediaAPI.url_fixURL(url.toString());

                if (result != null) {
                    this.url = result.url.toString();
                    assumeStream.set(result.assumeStream);

                    raw.mediaPlayer().media().prepare(this.url, vlcArgs);
                    started.set(true);
                } else LOGGER.error(IT, "Playback prepare failed. URL is invalid or null");
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to prepare player", e);
            }
        });
    }
    public void startPaused(CharSequence url) { this.prepare(url, new String[0]); }
    public void startPaused(CharSequence url, String[] vlcArgs) {
        if (raw == null) return;
        EX.execute(() -> {
            try {
                if (isPlaying()) stop();
                FixerBase.Result result = WaterMediaAPI.url_fixURL(url.toString());

                if (result != null) {
                    this.url = result.url.toString();
                    assumeStream.set(result.assumeStream);

                    raw.mediaPlayer().media().startPaused(this.url, vlcArgs);
                    started.set(true);
                } else LOGGER.error(IT, "Playback start paused failed. URL is invalid or null");
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to start paused player", e);
            }
        });
    }
    public State getRawPlayerState() {
        if (raw == null) return State.ERROR;
        synchronized (this) { return raw.mediaPlayer().status().state(); }
    }

    public void play() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().play(); }
    }

    public void pause() {
        if (raw == null) return;
        synchronized (this) { if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().pause(); }
    }

    public void setPauseMode(boolean isPaused) {
        if (raw == null) return;
        synchronized (this) { if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().setPause(isPaused); }
    }

    public void stop() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().stop(); }
    }

    public boolean isStarted() { return started.get(); }
    public boolean isBuffering() { return buffering.get(); }
    public boolean isPrepared() { return prepared.get(); }
    @Untested
    public boolean isPaused() { return getRawPlayerState().equals(State.PAUSED); }
    @Untested
    public boolean isStopped() { return getRawPlayerState().equals(State.STOPPED); }

    public boolean isValid() {
        if (raw == null) return false;
        synchronized (this) { return raw.mediaPlayer().media().isValid(); }
    }

    public boolean isPlaying() {
        if (raw == null) return false;
        synchronized (this) { return getRawPlayerState().equals(State.PLAYING); }
    }

    @Experimental
    public boolean isLive() {
        if (assumeStream.get()) return true;

        // MISSING IMPL
        return false;
    }

    @Deprecated
    public boolean isStream() {
        if (raw == null) return false;
        synchronized (this) {
            InfoApi mediaInfo = raw.mediaPlayer().media().info();
            return mediaInfo != null && (mediaInfo.type().equals(MediaType.STREAM) || mediaInfo.mrl().endsWith(".m3u") || mediaInfo.mrl().endsWith(".m3u8"));
        }
    }

    public boolean isSeekAble() {
        if (raw == null) return false;
        synchronized (this) { return raw.mediaPlayer().status().isSeekable(); }
    }

    public void seekTo(long time) {
        if (raw == null) return;
        synchronized (this) {
            post(new MediaTimeChangedEvent(this, getTime(), time));
            raw.mediaPlayer().controls().setTime(time);
        }
    }

    public void seekFastTo(long ticks) {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().setTime(ticks); }
    }

    /**
     * Use {@link MediaPlayerBase#seekTo(long)} in conjunction with {@link WaterMediaAPI#math_ticksToMillis(int)}
     * @deprecated is gonna being removed for 2.1.0
     */
    public void seekMineTo(int ticks) {
        if (raw == null) return;
        synchronized (this) {
            long time = WaterMediaAPI.math_ticksToMillis(ticks);
            post(new MediaTimeChangedEvent(this, getTime(), time));
            raw.mediaPlayer().controls().setTime(time);
        }
    }

    /**
     * Use {@link MediaPlayerBase#seekFastTo(long)} in conjunction with {@link WaterMediaAPI#math_ticksToMillis(int)}
     * @deprecated is gonna being removed for 2.1.0
     */
    public void seekMineFastTo(int ticks) {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().setTime(WaterMediaAPI.math_ticksToMillis(ticks)); }
    }

    public void foward() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().skipTime(5L); }
    }

    public void rewind() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().skipTime(-5L); }
    }

    public void setSpeed(float rate) {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().setRate(rate); }
    }

    public void setVolume(int volume) {
        this.volume.set(volume);
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().audio().setVolume(this.volume.get());
            if (this.volume.get() == 0 && !raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(true);
            else if (this.volume.get() > 0 && raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(false);
        }
    }

    public int getVolume() {
        if (raw == null) return volume.get();
        synchronized (this) { return raw.mediaPlayer().audio().volume(); }
    }

    public void mute() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().audio().setMute(true); }
    }

    public void unmute() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().audio().setMute(false); }
    }

    public void setMuteMode(boolean mode) {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().audio().setMute(mode); }
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    public long getDuration() {
        if (raw == null) return 0L;
        synchronized (this) {
            if (!isValid() || (RuntimeUtil.isNix() && getRawPlayerState().equals(State.STOPPED))) return 0L;
            return raw.mediaPlayer().status().length();
        }
    }

    /**
     * Use {@link MediaPlayerBase#getDuration()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
     * @deprecated is gonna being removed for 2.1.0
     */
    @Deprecated
    public int getMineDuration() {
        if (raw == null) return 0;
        synchronized (this) { return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().length()); }
    }

    public long getMediaInfoDuration() {
        if (raw == null) return 0L;
        synchronized (this) {
            InfoApi info = raw.mediaPlayer().media().info();
            if (info != null) return info.duration();
            return 0L;
        }
    }

    /**
     * Use {@link MediaPlayerBase#getMediaInfoDuration()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
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
        if (raw == null) return 0L;
        synchronized (this) { return raw.mediaPlayer().status().time(); }
    }

    /**
     * Use {@link MediaPlayerBase#getTime()} in conjunction with {@link WaterMediaAPI#math_millisToTicks(long)}
     * @deprecated is gonna being removed for 2.1.0
     */
    public int getMineTime() {
        if (raw == null) return 0;
        synchronized (this) { return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().time()); }
    }

    public boolean getRepeatMode() {
        if (raw == null) return false;
        synchronized (this) { return raw.mediaPlayer().controls().getRepeat(); }
    }
    public void setRepeatMode(boolean repeatMode) {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().controls().setRepeat(repeatMode); }
    }

    public void release() {
        if (raw == null) return;
        synchronized (this) { raw.mediaPlayer().release(); }
    }

    protected static void checkClassLoader() {
        if (Thread.currentThread().getContextClassLoader() == null) Thread.currentThread().setContextClassLoader(LOADER);
    }

    private final class WaterMediaPlayerEventListener implements MediaPlayerEventListener {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
            checkClassLoader();
            prepared.set(false);
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            checkClassLoader();
            post(new PlayerStateEvent.Prepare(MediaPlayerBase.this));
        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float newCache) {
            checkClassLoader();
            post(new MediaBufferingEvent.Progress(MediaPlayerBase.this, newCache));
            buffering.set(true);
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            checkClassLoader();
            if (buffering.get()) {
                post(new MediaBufferingEvent.End(MediaPlayerBase.this));
                buffering.set(false);
            }

            if (volume.get() == 0) setMuteMode(true);
            else setVolume(volume.get());

            if (!prepared.get()) post(new PlayerStateEvent.Started());
            else post(new MediaResumeEvent(MediaPlayerBase.this, getDuration()));
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            checkClassLoader();
            post(new MediaPauseEvent(MediaPlayerBase.this, getDuration()));
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            checkClassLoader();
            post(new MediaStoppedEvent(MediaPlayerBase.this, getDuration()));
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
            post(new MediaFinishedEvent(MediaPlayerBase.this, url.toString()));
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
            post(new PlayerVolumeUpdateEvent(MediaPlayerBase.this, getVolume(), (int) volume));
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
            post(new PlayerStateEvent.Error(MediaPlayerBase.this));
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            checkClassLoader();
            post(new PlayerStateEvent.Ready(MediaPlayerBase.this));

            prepared.set(true);
            setVolume(volume.get());
        }
    }
}
