package org.watermedia.api.player.videolan;

import org.lwjgl.opengl.GL12;
import org.watermedia.api.render.RenderAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.base.MediaPlayer;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferCleanupCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormatCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.RenderCallback;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class VideoPlayer extends BasePlayer implements RenderCallback, BufferFormatCallback, BufferCleanupCallback {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    private int width = 1;
    private int height = 1;
    private int size = width * height * 4;
    private boolean refresh = false;
    private boolean first = true;
    private final int texture;
    private final Semaphore semaphore = new Semaphore(1);
    private final Executor renderExecutor;
    private ByteBuffer[] buffers;

    /**
     * Creates a player instance
     * @param renderExecutor executor of render thread for an async task (normally <code>Minecraft.getInstance()</code>)
     */
    public VideoPlayer(Executor renderExecutor) { this(null, renderExecutor); }

    /**
     * Creates a player instance
     * @param factory custom MediaPlayerFactory instance
     * @param renderExecutor executor of render thread for an async task (normally <code>Minecraft.getInstance()</code>)
     */
    public VideoPlayer(MediaPlayerFactory factory, Executor renderExecutor) {
        super();
        this.texture = RenderAPI.createTexture();
        this.renderExecutor = renderExecutor;
        this.init(factory, this, this, this);
        if (raw() == null) {
            RenderAPI.deleteTexture(texture);
        } else {
            // HACK IN THE JANK
            this.raw().mediaPlayer().videoSurface().getVideoSurface().setSemaphore(semaphore);
        }
    }

    @Override
    public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
        this.refresh = true;
    }

    @Override
    public void allocatedBuffers(ByteBuffer[] buffers) {
        this.buffers = buffers;
        this.first = true;
    }

    @Override
    public void cleanupBuffers(ByteBuffer[] buffers) {
        this.buffers = null;
    }

    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        this.width = sourceWidth;
        this.height = sourceHeight;
        this.size = sourceWidth * sourceHeight * 4;
        this.first = true;

        // TODO: This might be wrong; https://wiki.videolan.org/Chroma/
        // TODO: is not wrong but is undocumented...
        //  WHY?
        return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[]{sourceWidth * 4}, new int[]{sourceHeight});
    }

    public int size() {
        return size;
    }

    /**
     * Media Buffer width
     * @return Buffer texture width in px
     */
    public int width() { return width; }

    /**
     * Media Buffer height
     * @return Buffer texture height in px
     */
    public int height() { return height; }

    /**
     * Uploads the buffer in the current state
     *
     * <p>Ensure execution on RenderThread</p>
     * @return
     */
    public int preRender() {
        RenderAPI.bindTexture(this.texture);
        try {
            semaphore.tryAcquire(1, TimeUnit.SECONDS);
            if (refresh && buffers != null && buffers.length > 0) {
                RenderAPI.uploadBuffer(buffers[0], texture, GL12.GL_RGBA, width, height, first);
                first = false;
            }
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RenderAPI.bindTexture(RenderAPI.NONE);
        return texture;
    }

    /**
     * Texture for OpenGL
     * @return texture id or -1 when player is in broken state
     */
    public int texture() {
        return texture;
    }

    /**
     * Returns a Dimension instance by VLC
     * @return current buffer dimensions, null if raw player isn't created or by any internal VLC error
     */
    public Dimension dimension() {
        if (raw() == null) return null;
        return raw().mediaPlayer().video().videoDimension();
    }

    /**
     * Releases all resources of the player
     */
    @Override
    public void release() {
        renderExecutor.execute(() -> RenderAPI.deleteTexture(texture));
        super.release();
    }
}