package org.watermedia.api.media;

import com.sun.jna.ptr.IntByReference;
import org.watermedia.api.MediaAPI;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.binding.internal.libvlc_media_player_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;

import java.net.URI;

public class VideoPlayer extends MediaPlayer {
    private IntByReference width;
    private IntByReference height;
    private libvlc_media_player_t player;

    public VideoPlayer() {
        super();
        this.width = new IntByReference();
        this.height = new IntByReference();
    }

    @Override
    public void start() {
        libvlc_media_t media = VideoLan4J.getMediaInstance(MediaAPI.videoLanFactory(), (URI) null);
        LibVlc.libvlc_media_player_set_media(player, media);
        LibVlc.libvlc_media_player_play(player);
        this.pause();
    }

    @Override
    public void startPaused() {

    }

    @Override
    public boolean startSync() {
        return false;
    }

    @Override
    public boolean startSyncPaused() {
        return false;
    }

    @Override
    public boolean resume() {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean pause(boolean paused) {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public boolean togglePlay() {
        return false;
    }

    @Override
    public boolean seek(long time) {
        return false;
    }

    @Override
    public boolean seekQuick(long time) {
        return false;
    }

    @Override
    public boolean foward() {
        return false;
    }

    @Override
    public boolean rewind() {
        return false;
    }

    @Override
    public float speed() {
        return 0;
    }

    @Override
    public boolean speed(float speed) {
        return false;
    }

    @Override
    public boolean repeat() {
        return false;
    }

    @Override
    public boolean repeat(boolean repeat) {
        return false;
    }

    @Override
    public boolean usable() {
        return false;
    }

    @Override
    public boolean loading() {
        return false;
    }

    @Override
    public boolean buffering() {
        return false;
    }

    @Override
    public boolean ready() {
        return false;
    }

    @Override
    public boolean paused() {
        return false;
    }

    @Override
    public boolean playing() {
        return false;
    }

    @Override
    public boolean stopped() {
        return false;
    }

    @Override
    public boolean ended() {
        return false;
    }

    @Override
    public boolean validSource() {
        return false;
    }

    @Override
    public boolean liveSource() {
        return false;
    }

    @Override
    public boolean canSeek() {
        return false;
    }

    @Override
    public long duration() {
        return 0;
    }

    @Override
    public long time() {
        return 0;
    }

    @Override
    public void release() {

    }

    @Override
    public void volume(float volume) {

    }

    @Override
    public float volume() {
        return 0;
    }

    @Override
    public void mute(boolean mute) {

    }

    @Override
    public boolean mute() {
        return false;
    }

    @Override
    public void quality(Quality quality) {

    }

    @Override
    public Quality quality() {
        return null;
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public int texture() {
        return 0;
    }
}
