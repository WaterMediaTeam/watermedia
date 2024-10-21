package me.srrapero720.watermedia.api.player.videolan;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.player.PlayerAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class VideoPlayer extends BasePlayer {
    private static final Marker IT = MarkerManager.getMarker("SyncVideoPlayer");

    private volatile int pbo = -1;
    private volatile int width = 1;
    private volatile int height = 1;
    private volatile int size = width * height * 4;
    private final int texture;
    private ByteBuffer buffer;
    private boolean first = false;
    private final Object sync = new Object();
    protected final Executor renderThreadExecutor;

    /**
     * Creates a player instance
     * @param renderThreadExecutor executor of render thread for an async task (normally <code>Minecraft.getInstance()</code>)
     * @deprecated Future replacement is a static method inside {@link PlayerAPI}.
     * PlayerAPI (including all Players) are intended to be rewrited in 2.1.0
     */
    @Deprecated
    public VideoPlayer(Executor renderThreadExecutor) { this(null, renderThreadExecutor); }

    /**
     * Creates a player instance
     * @param factory custom MediaPlayerFactory instance
     * @param renderThreadExecutor executor of render thread for an async task (normally <code>Minecraft.getInstance()</code>)
     * @deprecated Future replacement is a static method inside {@link PlayerAPI}.
     * PlayerAPI (including all Players) are intended to be rewrited in 2.1.0
     */
    public VideoPlayer(MediaPlayerFactory factory, Executor renderThreadExecutor) {
        super();
        this.renderThreadExecutor = renderThreadExecutor;
        this.texture = genSimpleTexture();

        this.init(factory, (mediaPlayer, nativeBuffers, bufferFormat) -> {
            // execyted off-thread
            if (mediaPlayer.isReleased() || this.pbo == -1 || this.buffer == null) return;
            synchronized (sync) {
                buffer.put(nativeBuffers[0].flip());
                buffer.flip();
            }
        }, (sourceWidth, sourceHeight) -> {
            int bufferSize = sourceWidth * sourceHeight * 4;
            if (bufferSize != this.size) {
                // executed off-thread too
                renderThreadExecutor.execute(() -> {
                    if (pbo != -1) deletePBO(pbo);
                    this.pbo = genSimplePBO(bufferSize);
                    this.width = sourceWidth;
                    this.height = sourceHeight;
                    this.buffer = GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_WRITE_ONLY);
                    GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);  // Unbind
                });
                this.size = bufferSize;
            }

            // TODO: This is wrong; https://wiki.videolan.org/Chroma/
            return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[]{sourceWidth * 4}, new int[]{sourceHeight});
        });
        if (raw() == null) {
            GL11.glDeleteTextures(texture);
        }
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

    public int preRender() {
        if (pbo == -1) return -1;
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pbo);
        synchronized (sync) {
            if (buffer != null) {
                GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
                if (!first) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL12.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);
                    this.first = true;
                } else {
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL12.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, 0L);
                }
            }

            this.buffer = GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_WRITE_ONLY);
        }

        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
     * Returns a Dimension instance with current byffer size
     * values are obtained after build IntBuffer first time
     * @return current buffer dimensions, null if raw player isn't created
     */
    public Dimension dimension() {
        if (raw() == null) return null;
        return new Dimension(width, height);
    }

    /**
     * Returns a Dimension instance by VLC
     * @return current buffer dimensions, null if raw player isn't created or by any internal VLC error
     */
    public Dimension mediaDimensions() {
        if (raw() == null) return null;
        return raw().mediaPlayer().video().videoDimension();
    }

    public static int genSimpleTexture() {
        final int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        // Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Unbind
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);  // Unbind

        return id;
    }

    public void deletePBO(int id) {
        if (this.buffer != null) {
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, id);
            GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);
        }
        GL15.glDeleteBuffers(id);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        this.buffer = null;
    }

    public static int genSimplePBO(int size) {
        int id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, id);
        GL15.glBufferData(GL21.GL_PIXEL_UNPACK_BUFFER, size, GL15.GL_STREAM_DRAW); // Size
        return id;
    }

    public static int uploadPBOTexture(int pbo, int texture, int width, int height) {
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pbo);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA8, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, 0);
        return texture;
    }

    /**
     * Releases all resources of the player
     */
    @Override
    public void release() {
        if (pbo != -1) {
            GL15.glDeleteBuffers(pbo);
        }
        renderThreadExecutor.execute(() -> {
            GL11.glDeleteTextures(texture);
        });
        super.release();
    }
}