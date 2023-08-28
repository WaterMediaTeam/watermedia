package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import me.lib720.watermod.concurrent.ThreadCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.core.tools.ReflectTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings("unused")
public class VideoPlayer extends BasePlayer {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    private volatile int texture;
    private volatile int width = 1;
    private volatile int height = 1;
    private volatile Buffer buffer;
    private volatile Throwable exception;

    private final boolean intBuffer;
    protected final ReentrantLock renderLock = new ReentrantLock();
    protected final AtomicBoolean updateFrame = new AtomicBoolean(false);
    protected final AtomicBoolean updateFirstFrame = new AtomicBoolean(false);

    public VideoPlayer(MediaPlayerFactory factory, boolean intBuffer, Executor playerThreadEx, MemoryAllocatorHelper memoryAllocatorHelper) {
        super(playerThreadEx);
        this.texture = GL11.glGenTextures();
        this.intBuffer = intBuffer;
        this.init(factory, (mediaPlayer, nativeBuffers, bufferFormat) -> {
            renderLock.lock();
            try {
                if (this.intBuffer) {
                    ((IntBuffer) buffer).put(nativeBuffers[0].asIntBuffer());
                    ReflectTool.executeMethod("rewind", buffer.getClass(), buffer);
                } else {
                    ReflectTool.executeMethod("rewind", nativeBuffers[0].getClass(), nativeBuffers[0]);
                    ((ByteBuffer) buffer).put(nativeBuffers[0]);
                    ReflectTool.executeMethod("rewind", buffer.getClass(), buffer);
                }
                updateFrame.set(true);
            } catch (Throwable t) {
                if (exception == null) {
                    exception = t;
                    LOGGER.fatal(IT, "Failed due process native buffers", t);
                }
            } finally {
                renderLock.unlock();
            }
        }, (sourceWidth, sourceHeight) -> {
            renderLock.lock();
            try {
                width = sourceWidth;
                height = sourceHeight;
                buffer = (intBuffer) ?
                        memoryAllocatorHelper.create(sourceWidth * sourceHeight * 4).asIntBuffer() :
                        memoryAllocatorHelper.create(sourceWidth * sourceHeight * 4);
                updateFrame.set(true);
                updateFirstFrame.set(true);
            } catch (Throwable t) {
                if (exception == null) {
                    exception = t;
                    LOGGER.fatal(IT, "Failed due create ByteBuffer", t);
                }
            } finally {
                renderLock.unlock();
            }
            return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[]{sourceWidth * 4}, new int[]{sourceHeight});
        });
    }

    public int prepareTexture() {
        if (raw() == null) return -1;
        renderLock.lock();
        try {
            if (updateFrame.compareAndSet(true, false))
                if (this.intBuffer) {
                    WaterMediaAPI.gl_applyBuffer((IntBuffer) buffer, texture, width, height, updateFirstFrame.compareAndSet(true, false));
                } else {
                    WaterMediaAPI.gl_applyBuffer((ByteBuffer) buffer, texture, width, height, updateFirstFrame.compareAndSet(true, false));
                }
            return texture;
        } finally {
            renderLock.unlock();
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTexture() { return texture; }

    public Dimension getDimensions() {
        if (raw() == null) return null;
        return ThreadCore.executeLock(playerLock, () -> {
            return raw().mediaPlayer().video().videoDimension();
        });
    }

    @Override
    public void release() {
        playerThreadEx.execute(() -> {
            GL11.glDeleteTextures(texture);
            texture = -1;
        });
        super.release();
    }

    public interface MemoryAllocatorHelper { ByteBuffer create(int size); }
}