package org.watermedia.api.player.videolan;

import com.sun.jna.Platform;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.api.player.PlayerAPI;
import org.watermedia.core.tools.ThreadTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.media.InfoApi;
import org.watermedia.videolan4j.media.MediaSlavePriority;
import org.watermedia.videolan4j.media.MediaSlaveType;
import org.watermedia.videolan4j.media.MediaType;
import org.watermedia.videolan4j.player.base.EmbededMediaPlayerEventListener;
import org.watermedia.videolan4j.player.base.MediaPlayer;
import org.watermedia.videolan4j.player.base.State;
import org.watermedia.videolan4j.player.component.CallbackMediaPlayerComponent;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferCleanupCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormatCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.RenderCallback;

import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.watermedia.WaterMedia.LOGGER;

public abstract class BasePlayer {
    protected static final Marker IT = MarkerManager.getMarker("BasePlayer");
    protected static final WaterMediaPlayerEventListener LISTENER = new WaterMediaPlayerEventListener();

    // PLAYER
    protected URI url;
    /**
     * @deprecated no replacement
     */
    @Deprecated
    private CallbackMediaPlayerComponent raw;
    /**
     * @deprecated no replacement
     */
    @Deprecated
    public CallbackMediaPlayerComponent raw() { return raw; }

    // PLAYER THREAD
    protected boolean live = false;
    protected ReentrantLock lock = new ReentrantLock();

    protected BasePlayer(MediaPlayerFactory factory, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        this.init(factory, renderCallback, bufferFormatCallback, cleanupCallback);
    }

    /**
     * This constructor skips raw player creation, instead waits for {@link #init(MediaPlayerFactory, RenderCallback, BufferFormatCallback, BufferCleanupCallback)} to create raw player
     * Intended to be used just in case you need to do some special implementations of {@link RenderCallback} or {@link BufferFormatCallback}
     */
    protected BasePlayer() {}

    /**
     * Creates a raw player and makes this works normally
     * @param factory MediaPlayerFactory to create raw player, can be null
     * @param renderCallback this is executed when buffer loads media info (first time)
     * @param bufferFormatCallback creates a buffer for the frame
     */
    protected void init(MediaPlayerFactory factory, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        if (PlayerAPI.isReady() && raw == null) {
            if (factory == null) factory = PlayerAPI.getFactory();
            this.raw = new CallbackMediaPlayerComponent(factory, true, renderCallback, bufferFormatCallback, cleanupCallback);
            // no bloat calls
            this.raw.mediaPlayer().input().enableKeyInputHandling(false);
            this.raw.mediaPlayer().input().enableMouseInputHandling(false);
            raw.mediaPlayer().events().addMediaPlayerEventListener(LISTENER);
        } else {
            LOGGER.error(IT, "Failed to create raw player because VLC is not loaded");
            this.raw = null;
        }
    }

    private boolean rpa(URI url) { // request player action
        if (raw == null) return false;
        try {
            AbstractPatch.Result result = NetworkAPI.patch(url);
            if (result == null) throw new IllegalArgumentException("Invalid URL");

            this.url = result.uri;
            this.live = result.assumeStream;
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load player", e);
            return false;
        }
    }

    public void start(URI url) { this.start(url, new String[0]); }
    public void start(URI url, String[] vlcArgs) {
        ThreadTool.thread(() -> {
            this.lock.lock();
            if (rpa(url)) {
                raw.mediaPlayer().media().play(this.url, vlcArgs);
            }
            this.lock.unlock();
        });
    }

    public void startPaused(URI url) { this.startPaused(url, new String[0]); }
    public void startPaused(URI url, String[] vlcArgs) {
        final String[] args = new String[vlcArgs.length + 1];
        System.arraycopy(vlcArgs, 0, args, 0, vlcArgs.length);
        args[vlcArgs.length] = "start-paused"; // pause on start

        ThreadTool.thread(() -> {
            this.lock.lock();
            if (rpa(url)) {
                raw.mediaPlayer().media().play(this.url, args);
            }
            this.lock.unlock();
        });
    }

    public void resume() {
        this.play();
    }

    public void play() {
        if (raw == null) return;
        raw.mediaPlayer().controls().play();
    }

    public void pause() {
        if (raw == null) return;
        if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().pause();
    }

    public void togglePlayback() {
        if (raw == null) return;
        if (isPaused()) {
            raw.mediaPlayer().controls().play();
        } else if (isPlaying()) {
            raw.mediaPlayer().controls().pause();
        }
    }

    public void setPauseMode(boolean pauseMode) {
        if (raw == null) return;
        if (raw.mediaPlayer().status().canPause()) raw.mediaPlayer().controls().setPause(pauseMode);
    }

    public void stop() {
        if (raw == null) return;
        raw.mediaPlayer().controls().stop();
    }

    /**
     * Check if any async task was active<br>
     * method {@link #release()} is also async, but it deletes raw player,
     * basically makes player instance useless.
     * @return true if any async task was active
     */
    public boolean isSafeUse() { return !lock.isLocked(); }

    public String getStateName() {
        return raw.mediaPlayer().status().state().name();
    }

    public boolean isWaiting() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.NOTHING_SPECIAL);
    }

    public boolean isLoading() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.OPENING);
    }

    public boolean isBuffering() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.BUFFERING);
    }
    public boolean isReady() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().isPlayable();
    }
    public boolean isPaused() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.PAUSED);
    }
    public boolean isStopped() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.STOPPED);
    }
    public boolean isEnded() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().state().equals(State.ENDED);
    }
    public boolean isMuted() {
        if (raw == null) return false;
        return raw.mediaPlayer().audio().isMute();
    }
    public boolean isBroken() {
        if (raw == null) return true;
        return raw.mediaPlayer().status().state().equals(State.ERROR);
    }

    public boolean isValid() {
        if (raw == null) return false;
        return raw.mediaPlayer().media().isValid();
    }
    public boolean isPlaying() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().isPlaying();
    }

    /**
     * Method is currently incomplete
     * it cannot be distingué if was a stream after get media information
     * that is supplied with our API but isn't enough, because another type of streams cannot be handled
     * @return if mrl was a livestream
     */
    public boolean isLive() {
        if (live) return true;
        // TODO: made a M3U8 headers reader, VLC can't provide this information
//        if (url.getPath().endsWith(".m3u8") || url.getPath().endsWith(".m3u")) {
//            if (getMediaInfoDuration() == -1) return true;
//            if (getTime() > getDuration()) return true;
//        }

        InfoApi info = raw.mediaPlayer().media().info();
        if (info != null) {
            return info.type().equals(MediaType.STREAM);
        }

        return false;
    }

    public boolean isSeekAble() {
        if (raw == null) return false;
        return raw.mediaPlayer().status().isSeekable();
    }

    public void seekTo(long time) {
        if (raw == null) return;
        raw.mediaPlayer().controls().setTime(time);
    }

    public void seekFastTo(long ticks) {
        if (raw == null) return;
        raw.mediaPlayer().controls().setTime(ticks);
    }

    public void foward() {
        if (raw == null) return;
        raw.mediaPlayer().controls().skipTime(5000L);
    }

    public void rewind() {
        if (raw == null) return;
        raw.mediaPlayer().controls().skipTime(-5000L);
    }

    public void setSpeed(float rate) {
        if (raw == null) return;
        raw.mediaPlayer().controls().setRate(rate);
    }

    public int getVolume() {
        if (raw == null) return 0;
        return raw.mediaPlayer().audio().volume();
    }

    public void setVolume(int volume) {
        if (raw == null) return;
        raw.mediaPlayer().audio().setVolume(volume);

        if (volume == 0 && !raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(true);
        else if (volume > 0 && raw.mediaPlayer().audio().isMute()) raw.mediaPlayer().audio().setMute(false);
    }

    public void mute() {
        if (raw == null) return;
        raw.mediaPlayer().audio().setMute(true);
    }

    public void unmute() {
        if (raw == null) return;
        raw.mediaPlayer().audio().setMute(false);
    }

    public void setMuteMode(boolean mode) {
        if (raw == null) return;
        raw.mediaPlayer().audio().setMute(mode);
    }

    /**
     * Equals to <pre>player.mediaPlayer().status().length()</pre>
     * @return Player duration
     */
    public long getDuration() {
        if (raw == null) return 0L;
        if (!isValid() || (Platform.isLinux() && isStopped())) return 0L;
        return raw.mediaPlayer().status().length();
    }

    public long getMediaInfoDuration() {
        if (raw == null) return 0L;
        InfoApi info = raw.mediaPlayer().media().info();
        if (info != null) return info.duration();
        return 0L;
    }

    public long getTime() {
        if (raw == null) return 0L;
        return raw.mediaPlayer().status().time();
    }

    public boolean getRepeatMode() {
        if (raw == null) return false;
        return raw.mediaPlayer().controls().getRepeat();
    }

    public void setRepeatMode(boolean repeatMode) {
        if (raw == null) return;
        raw.mediaPlayer().controls().setRepeat(repeatMode);
    }

    public void release() {
        if (raw == null) return;
        ThreadTool.thread(() -> {
            lock.lock();

            CallbackMediaPlayerComponent rawRef = raw;
            raw = null;

            // remove callbacks to prevent more blowup stuff
            if (rawRef == null) return; // If for some reason is triggered 2 times.
            rawRef.mediaPlayer().release();

            lock.unlock();
        });
    }

    protected static final class WaterMediaPlayerEventListener extends EmbededMediaPlayerEventListener {
        @Override
        public void buffering(MediaPlayer mediaPlayer, float newCache) {
            if (newCache >= 100) mediaPlayer.submit(() -> {
                mediaPlayer.audio().enforceVolume();

                int volume = mediaPlayer.audio().masterVolume();
                if (volume == 0 && !mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(true);
                else if (volume > 0 && mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(false);
            });
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            mediaPlayer.submit(() -> {
                mediaPlayer.audio().enforceVolume();

                int volume = mediaPlayer.audio().masterVolume();
                if (volume == 0 && !mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(true);
                else if (volume > 0 && mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(false);
            });
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            mediaPlayer.submit(() -> {
                mediaPlayer.audio().enforceVolume();

                int volume = mediaPlayer.audio().masterVolume();
                if (volume == 0 && !mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(true);
                else if (volume > 0 && mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(false);
            });
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            mediaPlayer.submit(() -> {
                mediaPlayer.audio().enforceVolume();

                int volume = mediaPlayer.audio().masterVolume();
                if (volume == 0 && !mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(true);
                else if (volume > 0 && mediaPlayer.audio().isMute()) mediaPlayer.audio().setMute(false);

                if (mediaPlayer.media().info().duration() < 500) {
                    mediaPlayer.controls().pause();
                }
            });
        }
    }
}