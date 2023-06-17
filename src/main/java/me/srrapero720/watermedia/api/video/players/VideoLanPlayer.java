package me.srrapero720.watermedia.api.video.players;

import me.lib720.caprica.vlcj.media.MediaRef;
import me.lib720.caprica.vlcj.media.TrackType;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.MediaPlayerEventListener;
import me.srrapero720.watermedia.api.MediaApiCore;
import me.srrapero720.watermedia.api.video.players.events.EventManager;
import me.srrapero720.watermedia.api.video.players.events.common.*;
import me.srrapero720.watermedia.vlc.VLCManager;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.awt.*;
import java.net.URL;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VideoLanPlayer extends Player {
    private boolean buffering = false;
    private boolean prepared = false;
    public final EventManager<VideoLanPlayer> events = new EventManager<>();

    private CallbackMediaPlayerComponent player;
    public CallbackMediaPlayerComponent getRawPlayerComponent() { return player; }

    @Deprecated(forRemoval = true)
    public VideoLanPlayer(@Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        this(VLCManager.getDefaultFactory(), renderCallback, bufferFormatCallback);
    }

    public VideoLanPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable BufferFormatCallback bufferFormatCallback) {
        if (factory == null) factory = VLCManager.getDefaultFactory();

        if (VLCManager.isLoaded()) this.player = this.init(factory, renderCallback, bufferFormatCallback);
        else LOGGER.error("[VideoLanPlayer] Failed to create CallbackMediaPlayerComponent because VLC is not loaded");
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
    public void prepare(@NotNull CharSequence url) { this.prepare(url, new String[0]); }
    public void prepare(@NotNull CharSequence url, String[] vlcArgs) {
        if (player == null) return;
        ThreadUtil.threadTry(() -> {
            super.start(url.toString());
            player.mediaPlayer().media().prepare(this.url, vlcArgs);
        }, null, null);
    }


    public Dimension getVideoDimension() {
        if (player != null) return player.mediaPlayer().video().videoDimension();
        return null;
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
        if (player == null) return;
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
        events.callMediaTimeChangedEvent(this, new MediaTimeChangedEvent.EventData(getTime(), time));
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
        var time = MediaApiCore.gameTicksToMs(ticks);
        events.callMediaTimeChangedEvent(this, new MediaTimeChangedEvent.EventData(getTime(), time));
        player.mediaPlayer().controls().setTime(time);
    }

    @Override
    public void seekGameTickFastTo(int ticks) {
        if (player == null) return;
        player.mediaPlayer().controls().setTime(MediaApiCore.gameTicksToMs(ticks));
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
        if (player == null) return;
        player.mediaPlayer().controls().setRate(rate);
    }

    @Override
    public void rewind() {
        if (player == null) return;
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
        return MediaApiCore.msToGameTicks(player.mediaPlayer().status().length());
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

    public int getGameTickStatusDuration() {
        if (player == null) return 0;
        var info = player.mediaPlayer().media().info();
        if (info != null) return MediaApiCore.msToGameTicks(info.duration());
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
        return MediaApiCore.msToGameTicks(player.mediaPlayer().status().time());
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
    }


    private CallbackMediaPlayerComponent init(MediaPlayerFactory factory, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback) {
        final var component = new CallbackMediaPlayerComponent(factory, null, null, false, renderCallback, bufferFormatCallback, null);
        final var $this = this;

        component.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override
            public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
                prepared = false;
            }

            @Override
            public void opening(MediaPlayer mediaPlayer) {
                events.callPlayerPreparingEvent($this, new PlayerPreparingEvent.EventData());
            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
                events.callPlayerBufferProgressEvent($this, new PlayerBuffer.EventProgressData(newCache));
                buffering = true;
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (buffering) events.callPlayerBufferEndEvent($this, new PlayerBuffer.EventEndData());
                buffering = false;

                if (!prepared) events.callPlayerStartedEvent($this, new PlayerStartedEvent.EventData());
                else events.callMediaResumeEvent($this, new MediaResumeEvent.EventData(player.mediaPlayer().status().length()));
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                events.callMediaPauseEvent($this, new MediaPauseEvent.EventData(player.mediaPlayer().status().length()));
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                events.callMediaStoppedEvent($this, new MediaStoppedEvent.EventData(player.mediaPlayer().status().length()));
            }

            @Override
            public void forward(MediaPlayer mediaPlayer) {

            }

            @Override
            public void backward(MediaPlayer mediaPlayer) {

            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                ThreadUtil.trySimple(() -> events.callMediaFinishEvent($this, new MediaFinishEvent.EventData(new URL(url))));
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {}

            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {}

            @Override
            public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {

            }

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
                events.callMediaVolumeUpdate($this, new MediaVolumeUpdateEvent.EventData($this.getVolume(), (int) volume));
            }

            @Override
            public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {

            }

            @Override
            public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {}

            @Override
            public void error(MediaPlayer mediaPlayer) {
                events.callPlayerExceptionEvent($this, new PlayerExceptionEvent.EventData(new RuntimeException("Something is wrong on VideoLanPlayer instance")));
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {
                prepared = true;
                events.callPlayerReadyEvent($this, new PlayerReadyEvent.EventData());
            }
        });

        return component;
    }
}

