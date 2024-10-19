package org.watermedia.api.media;

import org.watermedia.tools.DataTool;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageSource extends MediaSource {
    public final int[] textures;
    private final ByteBuffer[] images;

    // Clock calculation
    private long currentMS;
    private long marginTime = -1;
    private long[] delays;

    public ImageSource(BufferedImage image) {
        super(image.getWidth(), image.getHeight());
        this.duration = 0;
        this.images = new ByteBuffer[] {  };
        this.textures = new int[0];
    }

    public ImageSource(ByteBuffer buffer, int width, int height, long duration) {
        super(width, height);
        this.images = new ByteBuffer[] { buffer };
        this.textures = new int[0];
    }

    public ImageSource(ByteBuffer[] buffers, int width, int height, long[] delays) {
        super(width, height);
        this.images = buffers;
        this.delays = delays;
        this.duration = DataTool.sumArray(delays);
        this.textures = new int[0];
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
    public boolean repeaat() {
        return false;
    }

    @Override
    public boolean repeaat(boolean repeat) {
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
        if (marginTime == -1) {
            currentMS = marginTime = System.currentTimeMillis();
        }
        long start = (currentMS - marginTime);
        long end = System.currentTimeMillis() - marginTime;
        long time = end - start;

        this.currentMS = time;

        return textureInTime(time);
    }

    private int textureInTime(long time) {
        // if (textures == null) return 0;
        // if (textures.length == 0) return texture(0);
        for (int i = 0; i < delays.length; i++) {
            time -= delays[i];
            if (time <= 0)
                return texture(i);
        }
        return texture(/*images.length - 1*/);
    }

    private int texture(int index) {
        return 0;
    }
}
