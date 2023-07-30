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
    private final AtomicBoolean started = new AtomicBoolean(false);
    private volatile int volume = 100;
    private volatile CallbackMediaPlayerComponent player;
    public final EventManager<SafeVideoLANPlayer> EV = new EventManager<>();

    public CallbackMediaPlayerComponent raw() { return player; }

    public SafeVideoLANPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        // SAFE
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
        if (player == null) return;
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
        if (player == null) return;
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
        if (player == null) return;
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
        if (player == null) return;
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
        if (player == null) return;
        player.mediaPlayer().audio().setMute(true);
    }

    @Override
    public void unmute() {
        if (player == null) return;
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
        return player.mediaPlayer().status().length();
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
        player.mediaPlayer().release();
        player = null;
        started.set(false);
        volume = 0;
    }


    private CallbackMediaPlayerComponent init(MediaPlayerFactory factory, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback) {
        return new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
    }
}

