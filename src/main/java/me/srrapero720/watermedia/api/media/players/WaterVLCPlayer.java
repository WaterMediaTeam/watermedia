package me.srrapero720.watermedia.api.media.players;

import me.srrapero720.watermedia.internal.util.WaterUtil;
import me.srrapero720.watermedia.vlc.VLCManager;
import me.srrapero720.watermedia.internal.util.ThreadUtil;
import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.RenderCallback;

import javax.annotation.Nullable;

public class WaterVLCPlayer extends Player {
    protected CallbackMediaPlayerComponent player;
    public WaterVLCPlayer(String url, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(url, VLCManager.getDefaultFactory(), renderCallback, bufferFormatCallback);
    }

    public WaterVLCPlayer(String url, MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        super(url);
        this.player = new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
    }

    public CallbackMediaPlayerComponent getRawPlayer() {
        return player;
    }


    @Override
    public void start() {
        this.start(new String[0]);
    }

    @Override
    public void start(String url) {
        this.start(url, new String[0]);
    }

    public void start(String... vlcArgs) {
        if (player == null) return;
        ThreadUtil.thread(() -> player.mediaPlayer().media().start(this.url, vlcArgs));
    }

    public void start(String url, String... vlcArgs) {
        super.start(url);
        if (player == null) return;
        ThreadUtil.thread(() -> player.mediaPlayer().media().start(this.url, vlcArgs));
    }

    @Override
    public void play() {
        if (player == null) return;
        player.mediaPlayer().controls().play();
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

    @Override
    public long getDuration() {
        if (player == null) return 0L;
        var info = player.mediaPlayer().media().info();
        if (info != null) return info.duration();
        return 0L;
    }

    @Override
    public long getGameTickDuration() {
        if (player == null) return 0L;
        var info = player.mediaPlayer().media().info();
        if (info != null) return WaterUtil.msToGameTicks(info.duration());
        return 0L;
    }

    public long getMediaLength() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().length();
    }

    public long getGameTickMediaLength() {
        if (player == null) return 0L;
        return WaterUtil.msToGameTicks(player.mediaPlayer().status().length());
    }

    public long getTime() {
        if (player == null) return 0L;
        return player.mediaPlayer().status().time();
    }

    public long getGameTickTime() {
        if (player == null) return 0L;
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

