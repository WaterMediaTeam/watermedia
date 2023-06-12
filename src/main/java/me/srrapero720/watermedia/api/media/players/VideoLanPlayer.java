package me.srrapero720.watermedia.api.media.players;

import me.lib720.caprica.vlcj4.media.MediaRef;
import me.lib720.caprica.vlcj4.media.TrackType;
import me.lib720.caprica.vlcj4.player.base.MediaPlayer;
import me.lib720.caprica.vlcj4.player.base.MediaPlayerEventListener;
import me.srrapero720.watermedia.api.media.players.handler.event.*;
import me.srrapero720.watermedia.internal.util.WaterUtil;
import me.srrapero720.watermedia.vlc.VLCManager;
import me.srrapero720.watermedia.internal.util.ThreadUtil;
import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.RenderCallback;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VideoLanPlayer extends Player<VideoLanPlayer> {
    protected boolean buffering = false;
    protected CallbackMediaPlayerComponent player;

    @Deprecated(forRemoval = true)
    public VideoLanPlayer(String url, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(url, VLCManager.getDefaultFactory(), renderCallback, bufferFormatCallback);
    }

    @Deprecated(forRemoval = true)
    public VideoLanPlayer(String url, MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(factory, renderCallback, bufferFormatCallback);
        super.compat(url);
    }

    public VideoLanPlayer(@Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(VLCManager.getDefaultFactory(), renderCallback, bufferFormatCallback);
    }

    public VideoLanPlayer(MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        var _self = this;
        this.player = new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
        this.player.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {}

            @Override
            public void opening(MediaPlayer mediaPlayer) {
                for (var event: events) if (event instanceof PlayerStarting<VideoLanPlayer> ev) ev.call(_self, _self.url);
            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
                _self.buffering = true;
                for (var event: events) if (event instanceof PlayerBuffering<VideoLanPlayer> ev) ev.call(_self, newCache);
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                for (var event: events) {
                    if (event instanceof MediaPlaying<VideoLanPlayer> ev) ev.call(_self);
                    if (_self.buffering && event instanceof PlayerBufferingFinished<VideoLanPlayer> ev) {
                        ev.call(_self);
                    }
                }
                _self.buffering = false;
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                for (var event: events) if (event instanceof MediaPause<VideoLanPlayer> ev) ev.call(_self);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                for (var event: events) if (event instanceof MediaStopped<VideoLanPlayer> ev) ev.call(_self);
            }

            @Override
            public void forward(MediaPlayer mediaPlayer) {

            }

            @Override
            public void backward(MediaPlayer mediaPlayer) {

            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                for (var event: events) if (event instanceof MediaFinished<VideoLanPlayer> ev) ev.call(_self);
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {

            }

            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                for (var event: events) if (event instanceof MediaTimeChanged<VideoLanPlayer> ev) ev.call(_self, _self.getTime(), newPosition);
            }

            @Override
            public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {}

            @Override
            public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {}

            @Override
            public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {}

            @Override
            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {}

            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {}

            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int newCount) {}

            @Override
            public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {}

            @Override
            public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {}

            @Override
            public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType type, int id) {}

            @Override
            public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {}

            @Override
            public void corked(MediaPlayer mediaPlayer, boolean corked) {}

            @Override
            public void muted(MediaPlayer mediaPlayer, boolean muted) {}

            @Override
            public void volumeChanged(MediaPlayer mediaPlayer, float volume) {}

            @Override
            public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {}

            @Override
            public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {}

            @Override
            public void error(MediaPlayer mediaPlayer) {
                for (var event: events) if (event instanceof PlayerException<VideoLanPlayer> ev) ev.call(_self, null);
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {

            }
        });
    }

    public CallbackMediaPlayerComponent getRawPlayer() { return player; }

    @Override
    @Deprecated(forRemoval = true)
    public void start() { this.start(new String[0]); }

    @Deprecated(forRemoval = true)
    public void start(String[] vlcArgs) {
        if (player == null) return;
        ThreadUtil.threadTry(() -> player.mediaPlayer().media().start(this.url, vlcArgs), null, null);
    }

    @Override
    public void start(@NotNull CharSequence url) { this.start(url, new String[0]); }

    public synchronized void start(CharSequence url, String[] vlcArgs) {
        if (player == null) return;
        ThreadUtil.threadTry(() -> {
            super.start(url.toString());
            player.mediaPlayer().media().start(this.url, vlcArgs);
        }, null, null);
    }

    @Override
    public void play() {
        if (player == null) return;
        player.mediaPlayer().controls().play();
        for (var event: events) if (event instanceof MediaResume<VideoLanPlayer> ev) ev.call(this, this.getTime());
    }

    @Override
    public void pause() {
        if (player == null) return;
        if (player.mediaPlayer().status().canPause()) player.mediaPlayer().controls().pause();
    }

    @Override
    public void setPauseMode(boolean isPaused) {
        player.mediaPlayer().controls().setPause(isPaused);
    }

    @Override
    public void stop() {
        if (player == null) return;
        player.mediaPlayer().controls().stop();
    }

    @Override
    public void seekTo(long time) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(time);
    }

    @Override
    public void seekFastTo(long ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(ticks);
    }

    @Override
    public void seekGameTicksTo(int ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(WaterUtil.gameTicksToMs(ticks));
    }

    @Override
    public void seekGameTickFastTo(int ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(WaterUtil.gameTicksToMs(ticks));
    }

    @Override
    public void setRepeatMode(boolean repeatMode) {
        if (player == null) return;
        player.mediaPlayer().controls().setRepeat(repeatMode);
    }

    @Override
    public boolean isValid() {
        if (player == null) return false;
        return player.mediaPlayer().media().isValid();
    }

    @Override
    public boolean isPlaying() {
        if (player == null) return false;
        return player.mediaPlayer().status().isPlaying();
    }

    @Override
    public boolean getRepeatMode() {
        if (player == null) return false;
        return player.mediaPlayer().controls().getRepeat();
    }

    @Override
    public void fastFoward() {
        if (player == null) return;
        player.mediaPlayer().controls().skipTime(5L);
    }

    @Override
    public void setSpeed(float rate) {
        player.mediaPlayer().controls().setRate(rate);
    }

    @Override
    public void rewind() {
        player.mediaPlayer().controls().skipTime(-5L);
    }

    @Override
    public void setVolume(int volume) {
        if (player == null) return;
        player.mediaPlayer().audio().setVolume(volume);
    }

    @Override
    public int getVolume() {
        if (player == null) return 0;
        return player.mediaPlayer().audio().volume();
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    @Override
    public long getDuration() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().length();
    }

    @Override
    public int getGameTickDuration() {
        if (player == null) return 0;
        var info = player.mediaPlayer().media().info();
        if (info != null) return WaterUtil.msToGameTicks(info.duration());
        return 0;
    }

    /**
     * Equals to <pre>player.mediaPlayer().media().info().duration()</pre>
     * @return Media information about duration
     */
    public long getStatusDuration() {
        if (player == null) return 0L;

        var info = player.mediaPlayer().media().info();
        if (info != null) return info.duration();
        return 0L;
    }

    public int getGameTickMediaLength() {
        if (player == null) return 0;
        return WaterUtil.msToGameTicks(player.mediaPlayer().status().length());
    }

    public long getTime() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().time();
    }

    public int getGameTickTime() {
        if (player == null) return 0;
        return WaterUtil.msToGameTicks(player.mediaPlayer().status().time());
    }

    public boolean isSeekable() {
        if (player == null) return false;
        return player.mediaPlayer().status().isSeekable();
    }

    @Override
    public void release() {
        if (player == null) return;
        player.mediaPlayer().release();
        player = null;
    }
}

