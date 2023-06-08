package me.srrapero720.watermedia.api.media.players.sync;

import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.RenderCallback;
import me.srrapero720.watermedia.api.media.players.Player;
import me.srrapero720.watermedia.internal.util.ThreadUtil;
import me.srrapero720.watermedia.internal.util.TickMediaUtil;
import me.srrapero720.watermedia.vlc.VLCManager;

import javax.annotation.Nullable;

public class SyncVLCPlayer extends Player {
    protected volatile CallbackMediaPlayerComponent player;
    public SyncVLCPlayer(String url, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(url, VLCManager.getDefaultFactory(), renderCallback, bufferFormatCallback);
    }

    public SyncVLCPlayer(String url, MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        super(url);
        this.player = new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
    }

    public synchronized CallbackMediaPlayerComponent getRawPlayer() {
        return player;
    }


    @Override
    public synchronized void start() {
        this.start(new String[0]);
    }

    @Override
    public synchronized void start(String url) {
        this.start(url, new String[0]);
    }

    public synchronized void start(String... vlcArgs) {
        if (player == null) return;
        ThreadUtil.thread(() -> player.mediaPlayer().media().start(this.url, vlcArgs));
    }

    public synchronized void start(String url, String... vlcArgs) {
        super.start(url);
        if (player == null) return;
        ThreadUtil.thread(() -> player.mediaPlayer().media().start(this.url, vlcArgs));
    }

    @Override
    public synchronized void play() {
        if (player == null) return;
        player.mediaPlayer().controls().play();
    }

    @Override
    public synchronized void pause() {
        if (player == null) return;
        if (player.mediaPlayer().status().canPause()) player.mediaPlayer().controls().pause();
    }

    @Override
    public synchronized void setPauseMode(boolean isPaused) {
        player.mediaPlayer().controls().setPause(isPaused);
    }

    @Override
    public synchronized void stop() {
        if (player == null) return;
        player.mediaPlayer().controls().stop();
    }

    @Override
    public synchronized void seekTo(long time) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(time);
    }

    @Override
    public synchronized void seekFastTo(long ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(ticks);
    }

    @Override
    public synchronized void seekGameTicksTo(int ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(TickMediaUtil.gameTicksToMs(ticks));
    }

    @Override
    public synchronized void seekGameTickFastTo(int ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(TickMediaUtil.gameTicksToMs(ticks));
    }

    @Override
    public synchronized void setRepeatMode(boolean repeatMode) {
        if (player == null) return;
        player.mediaPlayer().controls().setRepeat(repeatMode);
    }

    @Override
    public synchronized boolean isValid() {
        if (player == null) return false;
        return player.mediaPlayer().media().isValid();
    }

    @Override
    public synchronized boolean isPlaying() {
        if (player == null) return false;
        return player.mediaPlayer().status().isPlaying();
    }

    @Override
    public synchronized boolean getRepeatMode() {
        if (player == null) return false;
        return player.mediaPlayer().controls().getRepeat();
    }

    @Override
    public synchronized void fastFoward() {
        if (player == null) return;
        player.mediaPlayer().controls().skipTime(5L);
    }

    @Override
    public synchronized void setSpeed(float rate) {
        player.mediaPlayer().controls().setRate(rate);
    }

    @Override
    public synchronized void rewind() {
        player.mediaPlayer().controls().skipTime(-5L);
    }

    @Override
    public synchronized void setVolume(int volume) {
        if (player == null) return;
        player.mediaPlayer().audio().setVolume(volume);
    }

    @Override
    public synchronized int getVolume() {
        if (player == null) return 0;
        return player.mediaPlayer().audio().volume();
    }

    @Override
    public synchronized long getDuration() {
        if (player == null) return 0L;
        var info = player.mediaPlayer().media().info();
        if (info != null) return info.duration();
        return 0L;
    }

    @Override
    public synchronized long getGameTickDuration() {
        if (player == null) return 0L;
        var info = player.mediaPlayer().media().info();
        if (info != null) return TickMediaUtil.msToGameTicks(info.duration());
        return 0L;
    }

    public synchronized long getMediaLength() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().length();
    }

    public synchronized long getGameTickMediaLength() {
        if (player == null) return 0L;
        return TickMediaUtil.msToGameTicks(player.mediaPlayer().status().length());
    }

    public synchronized long getTime() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().time();
    }

    public synchronized long getGameTickTime() {
        if (player == null) return 0L;
        return TickMediaUtil.msToGameTicks(player.mediaPlayer().status().time());
    }

    public synchronized boolean isSeekable() {
        if (player == null) return false;
        return player.mediaPlayer().status().isSeekable();
    }

    @Override
    public synchronized void release() {
        if (player == null) return;
        player.mediaPlayer().release();
        player = null;
    }
}

