package me.srrapero720.watermedia.api.player.vlc;

import me.srrapero720.watermedia.api.rendering.RenderAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VideoPlayer extends SimplePlayer {
    private static final Marker IT = MarkerManager.getMarker(VideoPlayer.class.getSimpleName());

    private int texture;
    private volatile int width = 1;
    private volatile int height = 1;
    private volatile ByteBuffer buffer;
    private volatile Throwable exception;

    protected final Object lock = new Object();
    protected final Executor renderExecutor;
    protected final AtomicBoolean updated = new AtomicBoolean(false);
    protected final AtomicBoolean firstFrame = new AtomicBoolean(true);
    protected boolean texSubMethod = false;

    /**
     * Creates a player instance
     * @param factory custom MediaPlayerFactory instance
     * @param renderExecutor executor of render thread for an async task (normally <code>Minecraft.getInstance()</code>)
     */
    public VideoPlayer(MediaPlayerFactory factory, Executor renderExecutor) {
        super();
        this.renderExecutor = renderExecutor;
        this.texture = GL11.glGenTextures();
        this.init(factory, (mediaPlayer, nativeBuffers, bufferFormat) -> {
            synchronized (lock) {
                try {
                    buffer = nativeBuffers[0];
                    ((Buffer) buffer).rewind();
                    updated.set(true);
                } catch (Throwable t) {
                    if (exception == null) {
                        exception = t;
                        LOGGER.fatal(IT, "Failed due process native buffers", t);
                        renderExecutor.execute(this::release);
                    }
                }
            }
        }, (sourceWidth, sourceHeight) -> {
            synchronized (lock) {
                try {
                    width = sourceWidth;
                    height = sourceHeight;
                } catch (Throwable t) {
                    if (exception == null) {
                        exception = t;
                        LOGGER.fatal(IT, "Failed due create ByteBuffer", t);
                        renderExecutor.execute(this::release);
                    }
                }
            }
            return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[]{sourceWidth * 4}, new int[]{sourceHeight});
        });

        if (raw() == null) {
            GL11.glDeleteTextures(texture);
            texture = -1;
        }
    }

    /**
     * Forces {@link RenderAPI#applyBuffer(ByteBuffer, int, int, int, boolean)} to always use glTexImage2D instead of glTexSubImage2D
     * By default for performance purposes we use glTexSubImage2D,
     * but it may cause rendering issues on Minecraft Screens
     * @param mode force always glTexImage2D
     */
    public void useTexSubMethod(boolean mode) { texSubMethod = mode; }

    /**
     * Media Buffer width
     * @return Buffer texture width in px
     */
    public int getWidth() { return width; }

    /**
     * Media Buffer height
     * @return Buffer texture height in px
     */
    public int getHeight() {
        return height;
    }

    /**
     * Send texture buffer to OpenGL using VideoPlayer texture id
     * always run this on render thread
     */
    public int getTexture() {
        if (raw() == null) return 0;
        synchronized (lock) {
            if (updated.compareAndSet(true, false))
                RenderAPI.applyBuffer(buffer, texture, width, height, firstFrame.compareAndSet(true, texSubMethod));
        }
        return texture;
    }

    /**
     * Returns a Dimension instance based on current Buffer size
     * values are obtained after build Buffer first time
     * @return current buffer dimensions, null if raw player isn't created
     */
    public Dimension getDimensions() {
        if (raw() == null) return null;
        return new Dimension(width, height);
    }

    /**
     * Returns a Dimension instance by VLC
     * @return current buffer dimensions, null if raw player isn't created or by any internal VLC error
     */
    public Dimension getPlayerDimensions() {
        if (raw() == null) return null;
        return raw().mediaPlayer().video().videoDimension();
    }

    /**
     * Releases all resources of the player
     */
    @Override
    public void release() {
        renderExecutor.execute(() -> {
            GL11.glDeleteTextures(texture);
            texture = -1;
        });
        super.release();
    }
}