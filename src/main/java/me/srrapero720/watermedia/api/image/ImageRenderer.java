package me.srrapero720.watermedia.api.image;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import me.srrapero720.watermedia.api.math.MathAPI;
import me.srrapero720.watermedia.api.rendering.RenderAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

public class ImageRenderer {
    public final int width;
    public final int height;
    public final long duration;
    public final long[] delay;
    private final BufferedImage[] images;

    // GL PARAMS
    public int texture = -1;
    public int bufferId = -1;
    public ByteBuffer buffer = null;

    /**
     * creates a new instance of an ImageRenderer
     * @param image picture to use
     * @deprecated Use instead {@link ImageAPI#renderer(BufferedImage)}
     * method is going to begin package-protected
     */
    @Deprecated
    public ImageRenderer(BufferedImage image) {
        if (image == null) throw new NullPointerException();
        this.images = new BufferedImage[] { image };
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.delay = new long[] { 0 };
        this.duration = 1;

        this.setupGL();
    }

    /**
     * creates a new instance of an ImageRenderer
     * @param decoder picture to use
     * @deprecated Use instead {@link ImageAPI#renderer(GifDecoder)}
     * method is going to begin package-protected
     */
    @Deprecated
    public ImageRenderer(GifDecoder decoder) {
        if (decoder == null) throw new NullPointerException();

        Dimension frameSize = decoder.getFrameSize();
        this.width = frameSize.width;
        this.height = frameSize.height;
        this.images = decoder.getFrameArray();
        this.delay = decoder.getDelayArray();
        this.duration = decoder.getDuration();
    }

    private void setupGL() {
        if (texture != -1) return;
        this.texture = GL11.glGenTextures();
        this.bufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, bufferId);
        GL15.glBufferData(GL21.GL_PIXEL_UNPACK_BUFFER, this.width * this.height * 4L, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);

        this.buffer = RenderAPI.createMapBuffer(this.width * this.height * 4);
    }

    /**
     * gets texture id based on time in millis
     * use API to calculate time
     * @param time time in millis
     * @return texture id usable on OpenGL
     * @see ImageRenderer#texture(int, long, boolean) too
     */
    public int texture(long time) {
        int last = texture(0);
        for (int i = 1; i < delay.length; i++) {
            if (delay[i] > time) break;
            last = texture(i);
        }
        return last;
    }

    /**
     * gets texture id from texture array
     * 2.0.32 - This method now uploads the buffer into texture
     * @param index image index
     * @return texture id usable on OpenGL
     */
    public int texture(int index) {
        BufferedImage image = images[index];
        DataBufferByte bufferByte = (DataBufferByte) image.getRaster().getDataBuffer();

        // BIND PBO
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, bufferId);

        // COPY TO PBO
        buffer.clear();
        buffer.put(bufferByte.getData());
        buffer.flip();

        // DRAIN PBO
        GL21.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
        GL21.glBufferData(GL21.GL_PIXEL_UNPACK_BUFFER, buffer, GL21.GL_STREAM_DRAW);

        // PARAMS
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);

        return texture;
    }

    /**
     * Calculate texture based on tick time (1s/20t) plus deltaTime (missing ms on ticks)
     * make tick count by yourself
     * @param tick Tick count
     * @param deltaTime extra ms to add
     * @param loop enable looping if tick count overflows duration
     * @return OpenGL texture ID
     */
    public int texture(long tick, long deltaTime, boolean loop) {
        long time = MathAPI.tickToMs(tick) + deltaTime;
        if (duration > 0 && time > duration && loop) time %= duration;
        return texture(time);
    }

    /**
     * Calculate texture based on tick time (1s/20t) plus deltaTime (missing ms on ticks)
     * make tick count by yourself
     * @param tick Tick count
     * @param deltaTime extra ms to add
     * @param loop enable looping if tick count overflows duration
     * @return OpenGL texture ID
     */
    public int texture(int tick, long deltaTime, boolean loop) {
        long time = MathAPI.tickToMs(tick) + deltaTime;
        if (duration > 0 && time > duration && loop) time %= duration;
        return texture(time);
    }

    /**
     * Calculate texture based on tick time (1s/20t) plus partialTicks (fraction of a tick)
     * make tick count by yourself
     * @param tick Tick count
     * @param partialTicks fraction of tick time
     * @param loop enable looping if tick count overflows duration
     * @return OpenGL texture ID
     */
    public int texture(long tick, float partialTicks, boolean loop) {
        return texture(tick, MathAPI.tickToMs(partialTicks), loop);
    }

    /**
     * Calculate texture based on tick time (1s/20t) plus partialTicks (fraction of a tick)
     * make tick count by yourself
     * @param tick Tick count
     * @param partialTicks fraction of tick time
     * @param loop enable looping if tick count overflows duration
     * @return OpenGL texture ID
     */
    public int texture(int tick, float partialTicks, boolean loop) {
        return texture(tick, MathAPI.tickToMs(partialTicks), loop);
    }

    /**
     * This method just drains buffers but not releases OpenGL texture
     */
    public void flush() {
        for (BufferedImage img: images) {
            img.flush();
        }
    }
    /**
     * This method drain buffers and release OpenGL textures
     */
    public void release() {
        GL11.glDeleteTextures(texture);
        flush();
    }
}
