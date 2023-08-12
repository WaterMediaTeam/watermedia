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
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.SimpleBufferFormatCallback;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.events.*;
import me.srrapero720.watermedia.core.VideoLAN;
import me.lib720.watermod.ThreadCore;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings("unused")
public class VideoPlayer extends AbstractPlayer {
    private static final ClassLoader LOADER = Thread.currentThread().getContextClassLoader();
    private static final Marker IT = MarkerFactory.getMarker("VideoPlayer");

    // PLAYER
    public final CallbackMediaPlayerComponent raw;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean buffering = new AtomicBoolean(false);
    private final AtomicBoolean prepared = new AtomicBoolean(false);
    private final AtomicInteger volume = new AtomicInteger(100);

    public VideoPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable SimpleBufferFormatCallback bufferFormatCallback) {
        if (factory == null) factory = VideoLAN.getFactory();
        this.raw = this.init(factory, renderCallback, bufferFormatCallback);
    }

    public VideoPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        if (factory == null) factory = VideoLAN.getFactory();
        this.raw = this.init(factory, renderCallback, bufferFormatCallback);
    }

    @Override
    public synchronized void start(CharSequence url) { this.start(url, new String[0]); }
    public synchronized void start(CharSequence url, String[] vlcArgs) {
        if (raw == null) return;
        ThreadCore.threadTry(() -> {
            super.start(url.toString());

            if (this.url != null) {
                raw.mediaPlayer().media().start(this.url.toString(), vlcArgs);
                started.set(true);
            }
            else LOGGER.error(IT, "Playback start failed. URL is invalid or null");
        }, (e) -> LOGGER.error(IT, "Failed to prepare player", e), null);
    }

    @Override
    public synchronized void prepare(CharSequence url) { this.prepare(url, new String[0]); }
    public synchronized void prepare(CharSequence url, String[] vlcArgs) {
        if (raw == null) return;
        ThreadCore.threadTry(() -> {
            super.start(url.toString());
            raw.mediaPlayer().media().prepare(this.url.toString(), vlcArgs);
            started.set(true);
        }, (e) -> LOGGER.error(IT, "Failed to prepare player", e), null);
    }

    public boolean isStarted() { return started.get(); }

    @Override
    public synchronized void play() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().play();
        }
    }

    @Override
    public synchronized void pause() {
        if (raw == null) return;
        synchronized (this) {
            if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().pause();
        }
    }

    @Override
    public synchronized void setPauseMode(boolean isPaused) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().setPause(isPaused);
        }
    }

    @Override
    public synchronized void stop() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().stop();
        }
    }

    @Override
    public synchronized void seekTo(long time) {
        if (raw == null) return;
        synchronized (this) {
            fireEvent(new MediaTimeChangedEvent(this, getTime(), time));
            raw.mediaPlayer().controls().setTime(time);
        }
    }

    @Override
    public synchronized void seekFastTo(long ticks) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().setTime(ticks);
        }
    }

    @Override
    public synchronized void seekGameTicksTo(int ticks) {
        if (raw == null) return;
        synchronized (this) {
            long time = WaterMediaAPI.math_ticksToMillis(ticks);
            fireEvent(new MediaTimeChangedEvent(this, getTime(), time));
            raw.mediaPlayer().controls().setTime(time);
        }
    }

    @Override
    public synchronized void seekGameTickFastTo(int ticks) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().setTime(WaterMediaAPI.math_ticksToMillis(ticks));
        }
    }

    @Override
    public synchronized void setRepeatMode(boolean repeatMode) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().setRepeat(repeatMode);
        }
    }

    @Override
    public synchronized boolean isValid() {
        if (raw == null) return false;
        synchronized (this) {
            return raw.mediaPlayer().media().isValid();
        }
    }

    @Override
    public synchronized Dimension getDimensions() {
        if (raw == null) return null;
        synchronized (this) {
            return raw.mediaPlayer().video().videoDimension();
        }
    }

    @Override
    public synchronized boolean isPlaying() {
        if (raw == null) return false;
        synchronized (this) {
            return getRawPlayerState().equals(State.PLAYING);
        }
    }

    @Override
    public synchronized boolean getRepeatMode() {
        if (raw == null) return false;
        synchronized (this) {
            return raw.mediaPlayer().controls().getRepeat();
        }
    }

    @Override
    public synchronized void fastFoward() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().skipTime(5L);
        }
    }

    @Override
    public synchronized void setSpeed(float rate) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().setRate(rate);
        }
    }

    @Override
    public synchronized void rewind() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().controls().skipTime(-5L);
        }
    }

    @Override
    public synchronized void setVolume(int volume) {
        this.volume.set(volume);
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().audio().setVolume(this.volume.get());
            if (this.volume.get() == 0 && !raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(true);
            else if (this.volume.get() > 0 && raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(false);
        }
    }

    @Override
    public synchronized int getVolume() {
        if (raw == null) return volume.get();
        synchronized (this) {
            return raw.mediaPlayer().audio().volume();
        }
    }

    @Override
    public synchronized void mute() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().audio().setMute(true);
        }
    }

    @Override
    public synchronized void unmute() {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().audio().setMute(false);
        }
    }

    @Override
    public synchronized void setMuteMode(boolean mode) {
        if (raw == null) return;
        synchronized (this) {
            raw.mediaPlayer().audio().setMute(mode);
        }
    }

    @Override
    public synchronized boolean isStream() {
        if (raw == null) return false;
        synchronized (this) {
            InfoApi mediaInfo = raw.mediaPlayer().media().info();
            return mediaInfo != null && (mediaInfo.type().equals(MediaType.STREAM) || mediaInfo.mrl().endsWith(".m3u") || mediaInfo.mrl().endsWith(".m3u8"));
        }
    }

    public synchronized State getRawPlayerState() {
        if (raw == null) return State.ERROR;
        synchronized (this) {
            return raw.mediaPlayer().status().state();
        }
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    @Override
    public synchronized long getDuration() {
        if (raw == null) return 0L;
        synchronized (this) {
            if (!isValid() || (RuntimeUtil.isNix() && getRawPlayerState().equals(State.STOPPED))) return 0L;
            return raw.mediaPlayer().status().length();
        }
    }

    @Override
    public synchronized int getGameTickDuration() {
        if (raw == null) return 0;
        synchronized (this) {
            return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().length());
        }
    }

    /**
     * Equals to <pre>player.mediaPlayer().media().info().duration()</pre>
     * @return Media information about duration
     */
    @Deprecated
    public synchronized long getMediaInfoDuration() {
        if (raw == null) return 0L;
        synchronized (this) {
            InfoApi info = raw.mediaPlayer().media().info();
            if (info != null) return info.duration();
            return 0L;
        }
    }

    @Deprecated
    public synchronized int getGameTickMediaInfoDuration() {
        if (raw == null) return 0;
        synchronized (this) {
            InfoApi info = raw.mediaPlayer().media().info();
            if (info != null) return WaterMediaAPI.math_millisToTicks(info.duration());
            return 0;
        }
    }

    @Override
    public synchronized long getTime() {
        if (raw == null) return 0L;
        synchronized (this) {
            return raw.mediaPlayer().status().time();
        }
    }

    @Override
    public synchronized int getGameTickTime() {
        if (raw == null) return 0;
        synchronized (this) {
            return WaterMediaAPI.math_millisToTicks(raw.mediaPlayer().status().time());
        }
    }

    @Override
    public synchronized boolean isSeekable() {
        if (raw == null) return false;
        synchronized (this) {
            return raw.mediaPlayer().status().isSeekable();
        }
    }

    @Override
    public synchronized void release() {
        if (raw == null) return;
        raw.mediaPlayer().release();
    }
    
    private CallbackMediaPlayerComponent init(MediaPlayerFactory factory, RenderCallback renderCallback, SimpleBufferFormatCallback bufferFormatCallback) {
        CallbackMediaPlayerComponent component = null;
        if (WaterMediaAPI.vlc_isReady()) {
            component = new CallbackMediaPlayerComponent(factory, false, renderCallback, bufferFormatCallback);
            component.mediaPlayer().events().addMediaPlayerEventListener(new CustomMediaPlayerEventListener());
        } else LOGGER.error(IT, "Failed to create raw player because VLC is not loaded");

        return component;
    }
    
    private static void checkIfCurrentThreadHasClassLoader() {
        if (Thread.currentThread().getContextClassLoader() == null) Thread.currentThread().setContextClassLoader(LOADER);
    }

    private final class CustomMediaPlayerEventListener implements MediaPlayerEventListener {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
            checkIfCurrentThreadHasClassLoader();
            prepared.set(false);
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new PlayerStateEvent.Prepare(VideoPlayer.this));
        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float newCache) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new MediaBufferingEvent.Progress(VideoPlayer.this, newCache));
            buffering.set(true);
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            if (buffering.get()) {
                fireEvent(new MediaBufferingEvent.End(VideoPlayer.this));
                buffering.set(false);
            }

            if (volume.get() == 0) setMuteMode(true);
            else setVolume(volume.get());

            if (!prepared.get()) fireEvent(new PlayerStateEvent.Started());
            else fireEvent(new MediaResumeEvent(VideoPlayer.this, getDuration()));
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new MediaPauseEvent(VideoPlayer.this, getDuration()));
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new MediaStoppedEvent(VideoPlayer.this, getDuration()));
        }

        @Override
        public void forward(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void backward(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new MediaFinishedEvent(VideoPlayer.this, url.toString()));
        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean corked) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean muted) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new PlayerVolumeUpdateEvent(VideoPlayer.this, getVolume(), (int) volume));
        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
            checkIfCurrentThreadHasClassLoader();
        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new PlayerStateEvent.Error(VideoPlayer.this));
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            checkIfCurrentThreadHasClassLoader();
            fireEvent(new PlayerStateEvent.Ready(VideoPlayer.this));

            prepared.set(true);
            setVolume(volume.get());
        }
    }
}

