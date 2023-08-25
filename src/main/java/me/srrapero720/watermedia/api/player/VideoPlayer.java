package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import me.lib720.watermod.concurrent.ThreadCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unused")
public class VideoPlayer extends BasePlayer {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    private volatile int texture;
    private volatile int width = 1;
    private volatile int height = 1;
    private volatile IntBuffer buffer;
    protected final ReentrantLock renderLock = new ReentrantLock();
    protected final AtomicBoolean updateFrame = new AtomicBoolean(false);
    protected final AtomicBoolean updateFirstFrame = new AtomicBoolean(false);

    public VideoPlayer(MediaPlayerFactory factory, PlayerThread playerThread, MemoryAllocatorHelper memoryAllocatorHelper) {
        super(playerThread);
        this.texture = GL11.glGenTextures();
        this.init(factory, (mediaPlayer, nativeBuffers, bufferFormat) -> {
            renderLock.lock();
            try {
                buffer.put(nativeBuffers[0].asIntBuffer());
                buffer.rewind();
                updateFrame.set(true);
            } finally {
                renderLock.unlock();
            }
        }, (sourceWidth, sourceHeight) -> {
            renderLock.lock();
            try {
                width = sourceWidth;
                height = sourceHeight;
                buffer = memoryAllocatorHelper.create(sourceWidth * sourceHeight * 4).asIntBuffer();
                updateFrame.set(true);
                updateFirstFrame.set(true);
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
                WaterMediaAPI.gl_applyBuffer(buffer, texture, width, height, updateFirstFrame.compareAndSet(true, false));
            return texture;
        } finally {
            renderLock.unlock();
        }
    }

    public int getTexture() { return texture; }

    public Dimension getDimensions() {
        if (raw() == null) return null;
        return ThreadCore.executeLock(playerLock, () -> {
            return raw().mediaPlayer().video().videoDimension();
        });
    }

    @Override
    public void release() {
        playerThread.askForExecution(() -> {
            GL11.glDeleteTextures(texture);
            texture = -1;
        });
        super.release();
    }

    public interface MemoryAllocatorHelper { ByteBuffer create(int size); }
}