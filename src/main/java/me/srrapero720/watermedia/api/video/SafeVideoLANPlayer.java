package me.srrapero720.watermedia.api.video;

import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.media.MediaRef;
import me.lib720.caprica.vlcj.media.MediaType;
import me.lib720.caprica.vlcj.media.TrackType;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.MediaPlayerEventListener;
import me.lib720.caprica.vlcj.player.base.State;
import me.lib720.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.api.video.callbacks.IThreadExecutor;
import me.srrapero720.watermedia.api.video.events.common.*;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class SafeVideoLANPlayer extends VideoPlayer {
    private static final Marker IT = MarkerFactory.getMarker("SafeVideoLanPlayer");
    private volatile boolean buffering = false;
    private volatile boolean prepared = false;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private volatile int volume = 100;
    private volatile long duration = -1;
    private volatile CallbackMediaPlayerComponent player;
    public final EventManager<SafeVideoLANPlayer> EV = new EventManager<>();

    // SAFE
    private final IThreadExecutor THREAD_EXECUTOR;
    public CallbackMediaPlayerComponent raw() { return player; }

    public SafeVideoLANPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback, IThreadExecutor threadExecutor) {
        if ((THREAD_EXECUTOR = threadExecutor) == null) throw new IllegalArgumentException("IThreadExecutor CANNOT be null");
        if (factory == null) factory = VideoLAN.factory();

        if (WaterMediaAPI.isVLCReady()) this.player = this.init(factory, renderCallback, bufferFormatCallback);
        else LOGGER.error(IT, "Failed to create CallbackMediaPlayerComponent because VLC is not loaded");
    }

    @Override
    public void start(@NotNull CharSequence url) { this.start(url, new String[0]); }
    public synchronized void start(CharSequence url, String[] vlcArgs) {
        if (player == null) return;
        ThreadUtil.threadTry(() -> {
            super.start(url.toString());
            player.mediaPlayer().media().start(this.url, vlcArgs);
            started.set(true);
        }, null, null);
    }

    @Override
    public void prepare(@NotNull CharSequence url) { this.prepare(url, new String[0]); }
    public void prepare(@NotNull CharSequence url, String[] vlcArgs) {
        if (player == null || !started.get()) return;
        ThreadUtil.threadTry(() -> {
            super.start(url.toString());
            player.mediaPlayer().media().prepare(this.url, vlcArgs);
        }, null, null);
    }

    @Override
    public void play() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().play();
    }

    @Override
    public void pause() {
        if (player == null || !started.get()) return;
        if (player.mediaPlayer().status().canPause()) player.mediaPlayer().controls().pause();
    }

    @Override
    public void setPauseMode(boolean isPaused) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().setPause(isPaused);
    }

    @Override
    public void stop() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().stop();
    }

    @Override
    public void seekTo(long time) {
        if (player == null || !started.get()) return;
        EV.callMediaTimeChangedEvent(this, new MediaTimeChangedEvent.EventData(getTime(), time));
        player.mediaPlayer().controls().setTime(time);
    }

    @Override
    public void seekFastTo(long ticks) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().setTime(ticks);
    }

    @Override
    public void seekGameTicksTo(int ticks) {
        if (player == null || !started.get()) return;
        var time = WaterMediaAPI.gameTicksToMs(ticks);
        EV.callMediaTimeChangedEvent(this, new MediaTimeChangedEvent.EventData(getTime(), time));
        player.mediaPlayer().controls().setTime(time);
    }

    @Override
    public void seekGameTickFastTo(int ticks) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().setTime(WaterMediaAPI.gameTicksToMs(ticks));
    }

    @Override
    public void setRepeatMode(boolean repeatMode) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().setRepeat(repeatMode);
    }

    @Override
    public boolean isValid() {
        if (player == null || !started.get()) return false;
//        if (RuntimeUtil.isNix()) {
//            if (!getRawPlayerState().equals(State.ENDED) && !getRawPlayerState().equals(State.ERROR) && !getRawPlayerState().equals(State.OPENING) && !getRawPlayerState().equals(State.NOTHING_SPECIAL)) {
//                return player.mediaPlayer().media().isValid();
//            }
//        } else return player.mediaPlayer().media().isValid();

        return player.mediaPlayer().media().isValid();
    }

    @Override
    public Dimension getDimensions() {
        if (player == null || !started.get()) return null;
        return player.mediaPlayer().video().videoDimension();
    }

    @Override
    public boolean isPlaying() {
        if (player == null || !started.get()) return false;
        return player.mediaPlayer().status().isPlaying() || getRawPlayerState().equals(State.PLAYING);
    }

    @Override
    public boolean getRepeatMode() {
        if (player == null || !started.get()) return false;
        return player.mediaPlayer().controls().getRepeat();
    }

    @Override
    public void fastFoward() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().skipTime(5L);
    }

    @Override
    public void setSpeed(float rate) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().setRate(rate);
    }

    @Override
    public void rewind() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().controls().skipTime(-5L);
    }

    @Override
    public synchronized void setVolume(int volume) {
        this.volume = volume;
//        if (RuntimeUtil.isNix() && !isValid()) return;
        if (player == null || !started.get()) return;
        player.mediaPlayer().audio().setVolume(this.volume);
        if (this.volume == 0 && !player.mediaPlayer().audio().isMute()) player.mediaPlayer().audio().setMute(true);
        else if (this.volume > 0 && player.mediaPlayer().audio().isMute()) player.mediaPlayer().audio().setMute(false);
    }

    @Override
    public int getVolume() {
        if (player == null || !started.get()) return volume;
        return player.mediaPlayer().audio().volume();
    }

    @Override
    public void mute() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().audio().mute();
    }

    @Override
    public void unmute() {
        if (player == null || !started.get()) return;
        player.mediaPlayer().audio().setMute(false);
    }

    @Override
    public void setMuteMode(boolean mode) {
        if (player == null || !started.get()) return;
        player.mediaPlayer().audio().setMute(mode);
    }

    @Override
    public boolean isStream() {
        if (player == null || !started.get()) return false;
        var mediaInfo = player.mediaPlayer().media().info();
        return mediaInfo != null && (mediaInfo.type().equals(MediaType.STREAM) || mediaInfo.mrl().endsWith(".m3u") || mediaInfo.mrl().endsWith(".m3u8"));
    }

    public State getRawPlayerState() {
        if (player == null || !started.get()) return State.NOTHING_SPECIAL;
        return player.mediaPlayer().status().state();
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    @Override
    public long getDuration() {
        if (player == null || !started.get()) return 0L;
//        if (!isValid() || (RuntimeUtil.isNix() && getRawPlayerState().equals(State.STOPPED))) return 0L;
        return duration = player.mediaPlayer().status().length();
    }

    @Override
    public int getGameTickDuration() {
        if (player == null) return 0;
        return WaterMediaAPI.msToGameTicks(player.mediaPlayer().status().length());
    }

    /**
     * Equals to <pre>player.mediaPlayer().media().info().duration()</pre>
     * @return Media information about duration
     */
    @Deprecated
    public long getMediaInfoDuration() {
        if (player == null) return 0L;

        var info = player.mediaPlayer().media().info();
        if (info != null) return info.duration();
        return 0L;
    }

    @Deprecated
    public int getGameTickMediaInfoDuration() {
        if (player == null) return 0;
        var info = player.mediaPlayer().media().info();
        if (info != null) return WaterMediaAPI.msToGameTicks(info.duration());
        return 0;
    }

    @Override
    public long getTime() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().time();
    }

    @Override
    public int getGameTickTime() {
        if (player == null) return 0;
        return WaterMediaAPI.msToGameTicks(player.mediaPlayer().status().time());
    }

    @Override
    public boolean isSeekable() {
        if (player == null) return false;
        return player.mediaPlayer().status().isSeekable();
    }

    @Override
    public void release() {
        if (player == null) return;
        player.mediaPlayer().events().removeMediaPlayerEventListener(eventListeners);
        player.mediaPlayer().release();
        player = null;
        started.set(false);
        volume = 0;
        buffering = false;
    }


    private CallbackMediaPlayerComponent init(MediaPlayerFactory factory, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback) {
        final var component = new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
        component.mediaPlayer().events().addMediaPlayerEventListener(eventListeners);
        return component;
    }

    private final MediaPlayerEventListener eventListeners = new MediaPlayerEventListener() {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
            THREAD_EXECUTOR.execute(() -> prepared = false);
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> EV.callPlayerPreparingEvent(SafeVideoLANPlayer.this, new PlayerPreparingEvent.EventData()));
        }

        @Override
        public void buffering(final MediaPlayer mediaPlayer, final float newCache) {
            THREAD_EXECUTOR.execute(() -> {
                EV.callPlayerBufferProgressEvent(SafeVideoLANPlayer.this, new PlayerBuffer.EventProgressData(newCache));
                buffering = true;
            });
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> {
                if (buffering) EV.callPlayerBufferEndEvent(SafeVideoLANPlayer.this, new PlayerBuffer.EventEndData());
                buffering = false;

                if (!prepared) EV.callPlayerStartedEvent(SafeVideoLANPlayer.this, new PlayerStartedEvent.EventData());
                else EV.callMediaResumeEvent(SafeVideoLANPlayer.this, new MediaResumeEvent.EventData(player.mediaPlayer().status().length()));

                mediaPlayer.audio().setVolume(volume);
            });
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> {
                mediaPlayer.audio().setVolume(volume);
                EV.callMediaPauseEvent(SafeVideoLANPlayer.this, new MediaPauseEvent.EventData(player.mediaPlayer().status().length()));
            });

        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> {
                long current = RuntimeUtil.isWindows() ? mediaPlayer.status().length() : duration;
                EV.callMediaStoppedEvent(SafeVideoLANPlayer.this, new MediaStoppedEvent.EventData(current));
            });
        }

        @Override
        public void forward(MediaPlayer mediaPlayer) {}

        @Override
        public void backward(MediaPlayer mediaPlayer) {}

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> ThreadUtil.trySimple(() -> EV.callMediaFinishEvent(SafeVideoLANPlayer.this, new MediaFinishEvent.EventData(new URL(url)))));
        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long newTime) {}

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {}

        @Override
        public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {}

        @Override
        public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
        }

        @Override
        public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {

        }

        @Override
        public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {

        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {

        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {

        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType type, int id) {

        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {
        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean corked) {

        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean muted) {

        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
            THREAD_EXECUTOR.execute(() -> EV.callMediaVolumeUpdate(SafeVideoLANPlayer.this, new MediaVolumeUpdateEvent.EventData(SafeVideoLANPlayer.this.getVolume(), (int) volume)));
        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
        }

        @Override
        public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {

        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> EV.callPlayerExceptionEvent(SafeVideoLANPlayer.this, new PlayerExceptionEvent.EventData(new RuntimeException("Something is wrong on VideoLanPlayer instance"))));
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            THREAD_EXECUTOR.execute(() -> {
                prepared = true;
                EV.callPlayerReadyEvent(SafeVideoLANPlayer.this, new PlayerReadyEvent.EventData());
                SafeVideoLANPlayer.this.player.mediaPlayer().audio().setVolume(volume);
            });
        }
    };
}

