package me.srrapero720.watermedia.api.player.videolan;

import me.srrapero720.watermedia.api.player.PlayerAPI;
import me.srrapero720.watermedia.api.rendering.RenderAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class VideoPlayer extends BasePlayer {
    private static final Marker IT = MarkerManager.getMarker("SyncVideoPlayer");

    private int pbo = -1;
    private int width = 1;
    private int height = 1;
    private final int texture;
    private ByteBuffer buffer;
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
        this.texture = RenderAPI.genSimpleTexture();

        this.init(factory, (mediaPlayer, nativeBuffers, bufferFormat) -> {
            if (mediaPlayer.isReleased() || buffer == null || this.pbo == -1) return;
            synchronized (sync) {
                buffer.put(nativeBuffers[0]);
                buffer.flip();
            }
        }, (sourceWidth, sourceHeight) -> {
            renderThreadExecutor.execute(() -> {
                if (pbo == -1) RenderAPI.deletePBO(pbo);
                pbo = RenderAPI.genSimplePBO(sourceWidth * sourceHeight * 4);
                width = sourceWidth;
                height = sourceHeight;
                buffer = GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_WRITE_ONLY);
            });

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
            GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);
            RenderAPI.uploadPBOTexture(pbo, texture, width, height);
            this.buffer = GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_WRITE_ONLY, buffer.capacity(), buffer);
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

    /**
     * Releases all resources of the player
     */
    @Override
    public void release() {
        if (pbo != -1) {
            RenderAPI.deletePBO(pbo);
        }
        renderThreadExecutor.execute(() -> {
            GL11.glDeleteTextures(texture);

        });
        super.release();
    }
}