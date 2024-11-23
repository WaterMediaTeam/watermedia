package org.watermedia.api.media;

import com.sun.jna.ptr.IntByReference;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_player_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;

import java.awt.*;

public class VideoLanSource extends MediaSource {
    private final IntByReference width = new IntByReference();
    private final IntByReference height = new IntByReference();
    private final libvlc_instance_t core;
    private final libvlc_media_player_t raw;

    public VideoLanSource() {
        this.raw = null;
        this.core = null;
    }

    @Override
    public int width() {
        int result = LibVlc.libvlc_video_get_size(this.raw, 0, this.width, this.height);
        if (result != 0)
            return 0;

        return this.width.getValue();
    }

    @Override
    public int height() {
        int result = LibVlc.libvlc_video_get_size(this.raw, 0, this.width, this.height);
        if (result != 0)
            return 0;

        return this.height.getValue();
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean startPaused() {
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
    public int texture() {
        return 0;
    }
}
