package org.watermedia.api.player.videolan;

import org.lwjgl.opengl.GL12;
import org.watermedia.api.rendering.RenderAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.base.MediaPlayer;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormatCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.RenderCallback;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class VideoPlayer extends BasePlayer implements RenderCallback, BufferFormatCallback {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    private int width = 1;
    private int height = 1;
    private int size = width * height * 4;
    private boolean refresh = false;
    private boolean first = true;
    private final int texture;
    private final Object renderSync = new Object();
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
        this.init(factory, this, this);
        if (raw() == null) {
            RenderAPI.deleteTexture(texture);
        }
    }

    @Override
    public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
        synchronized (renderSync) {
            this.refresh = true;
            this.buffers = nativeBuffers;
        }
    }

    @Override
    public void allocatedBuffers(ByteBuffer[] buffers) {
    }

    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        synchronized (renderSync) {
            this.width = sourceWidth;
            this.height = sourceHeight;
            this.size = sourceWidth * sourceHeight * 4;
        }

        // TODO: This is wrong; https://wiki.videolan.org/Chroma/
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
        synchronized (renderSync) {
            if (refresh && buffers != null && buffers.length > 0) {
                RenderAPI.uploadBuffer(buffers[0], texture, GL12.GL_RGBA, width, height, first);
                first = false;
            }
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