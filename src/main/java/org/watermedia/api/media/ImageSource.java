package org.watermedia.api.media;

import org.watermedia.api.MathAPI;
import org.watermedia.api.MemoryAPI;
import org.watermedia.api.RenderAPI;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.ThreadTool;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ImageSource extends MediaSource {
    private static final List<ImageSource> ACTIVE_MEDIA = new ArrayList<>();

    // media info
    private final int[] widths;
    private final int[] heights;
    private final long[] delays;
    public final int texture = RenderAPI.genTexture();
    private final ByteBuffer[] buffers;

    // State
    private final long duration;
    private int textureIndex = 0;
    private float speed = 1.0f;
    private boolean repeat = true;
    private boolean firstFrame = true;
    private State state = State.WAITING;
    private final Deque<State> queueState = new ConcurrentLinkedDeque<>();

    // CLOCK
    private long time;
    private long systemTime = System.currentTimeMillis();
    private boolean clock = false;

    public ImageSource(BufferedImage image) {
        this(new BufferedImage[] { image }, new long[1]);
    }

    public ImageSource(BufferedImage[] images, long[] delays) {
        this(
                DataTool.getValueFrom(images, RenderAPI::getByteBuffer),
                DataTool.getIntValueFrom(images, BufferedImage::getWidth),
                DataTool.getIntValueFrom(images, BufferedImage::getHeight),
                delays
        );
    }

    public ImageSource(ByteBuffer buffer, int width, int height) {
        this(new ByteBuffer[] { buffer }, new int[] { width }, new int[] { height }, new long[1]);
    }

    public ImageSource(ByteBuffer[] buffers, int[] width, int[] height, long[] delays) {
        this.widths = width;
        this.heights = height;
        this.buffers = buffers;
        this.delays = delays;
        this.duration = MathAPI.sumArray(delays);
    }

    @Override
    public int width() {
        return widths[textureIndex];
    }

    @Override
    public int height() {
        return heights[textureIndex];
    }

    @Override
    public boolean start() {
        this.state = State.PLAYING;
        return true;
    }

    @Override
    public boolean startPaused() {
        this.state = State.PAUSED;
        return true;
    }

    @Override
    public boolean resume() {
        // TODO: we should not ensure any other state?
        this.state = State.PLAYING;
        return true;
    }

    @Override
    public boolean pause() {
        // TODO: we should not ensure any other state?
        this.state = State.PAUSED;
        return true;
    }

    @Override
    public boolean pause(boolean paused) {
        // TODO: we should not ensure any other state?
        this.state = paused ? State.PAUSED : State.PLAYING;
        return false;
    }

    @Override
    public boolean stop() {
        // TODO: we should not ensure any other state?
        this.state = State.STOPPED;
        return true;
    }

    @Override
    public boolean togglePlay() {
        if (this.state == State.PLAYING || this.state == State.PAUSED) {
            this.state = this.state == State.PLAYING ? State.PAUSED : State.PLAYING;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean seek(long time) {
        if (time > duration) {
            time = time % duration;
        }
        if (time < 0) {
            time = 0;
        }
        this.time = time;
        return true;
    }

    @Override
    public boolean seekQuick(long time) {
        return seek(time);
    }

    @Override
    public boolean foward() {
        return seek(time + 5000L);
    }

    @Override
    public boolean rewind() {
        return seek(time - 5000L);
    }

    @Override
    public boolean speed(float speed) {
        if (speed < 0 || speed > 2) {
            return false;
        }
        this.speed = speed;
        return true;
    }

    @Override
    public boolean repeat() {
        return this.repeat(true);
    }

    @Override
    public boolean repeat(boolean repeat) {
        this.repeat = repeat;
        return true;
    }

    @Override
    public boolean usable() {
        return true; // once created is ready
    }

    @Override
    public boolean loading() {
        return this.state == State.LOADING; // once created is ready
    }

    @Override
    public boolean buffering() {
        return this.state == State.BUFFERING; // no buffering
    }

    @Override
    public boolean ready() {
        return this.state != State.ERROR; // once created is ready
    }

    @Override
    public boolean paused() {
        return this.state == State.PAUSED;
    }

    @Override
    public boolean playing() {
        return this.state == State.PLAYING;
    }

    @Override
    public boolean stopped() {
        return this.state == State.STOPPED;
    }

    @Override
    public boolean ended() {
        return this.state == State.ENDED;
    }

    @Override
    public boolean validSource() {
        return true;
    }

    @Override
    public boolean liveSource() {
        return false;
    }

    @Override
    public boolean canSeek() {
        return duration > 0;
    }

    @Override
    public long duration() {
        return duration;
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public void release() {
        this.state = State.ENDED;
        RenderAPI.delTexture(texture); // free GPU memory
        MemoryAPI.deallocate(buffers); // free RAM
        Arrays.fill(buffers, null);
    }

    @Override
    public int texture() {
        return textureInTime(time);
    }

    private void setState(State state) {
        queueState.add(state);
        switch (state) {

        }
    }

    // calculate texture
    private int textureInTime(long time) {
        for (int i = 0; i < delays.length; i++) {
            time -= delays[i];
            if (time <= 0) {
                uploadTexture(i);
                return texture;
            }
        }
        uploadTexture(buffers.length - 1);
        return texture;
    }

    // upload texture
    private void uploadTexture(int index) {
        if (buffers[index] == null)
            throw new IllegalStateException("Current MediaSource is released");
        RenderAPI.uploadBuffer(buffers[index], texture, widths[index], heights[index], firstFrame);
        firstFrame = false;
    }

    private void run() {
        // calculate delta
        long delta = System.currentTimeMillis() - this.systemTime;

        // Update state
        State state = queueState.peek();
        if (state != null) {
            switch (state) {
                case WAITING, LOADING, PAUSED, STOPPED, BUFFERING, ENDED, ERROR -> {
                    clock = false;
                }
                case PLAYING -> clock = true;
            }
            this.state = state;
        }

        // compute clocking
        if (clock) {
            time = Math.max(time + delta, duration); // start counting
        }
        this.systemTime = System.currentTimeMillis();

        if (time == duration) { // as expected
            if (this.repeat) {
                this.time = 0;
            } else {
                this.state = State.ENDED;
                this.queueState.clear();
            }
        }


    }
}
