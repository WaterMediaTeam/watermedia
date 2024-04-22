package me.srrapero720.watermedia.api.image;

import me.srrapero720.watermedia.api.image.decoders.GifDecoder;
import me.srrapero720.watermedia.api.math.MathAPI;
import me.srrapero720.watermedia.api.rendering.RenderAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageRenderer {
    public final int width;
    public final int height;
    public final long duration;
    public final long[] delay;
    private final BufferedImage[] images;

    // GL PARAMS
    public int texture = -1;
    public int pboId = -1;
    public int lastIndex = -1;
    public ByteBuffer pbo = null;

    /**
     * creates a new instance of an ImageRenderer
     * @param image picture to use
     * method is going to begin package-protected
     */
    ImageRenderer(BufferedImage image) {
        if (image == null) throw new NullPointerException();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.images = new BufferedImage[] { image };
        this.delay = new long[] { 0 };
        this.duration = 1;
    }

    /**
     * creates a new instance of an ImageRenderer
     * @param decoder picture to use
     * method is going to begin package-protected
     */
    ImageRenderer(GifDecoder decoder) {
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
        this.pboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pboId);
        GL15.glBufferData(GL21.GL_PIXEL_UNPACK_BUFFER, this.width * this.height * 4L, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
    }

    /**
     * gets texture id based on time in millis
     * use API to calculate time
     * @param time time in millis
     * @return texture id usable on OpenGL
     * @see ImageRenderer#texture(int, long, boolean) too
     */
    public int texture(long time) {
        for (int i = 0; i < delay.length; i++) {
            time -= delay[i];
            if (time <= 0)
                return texture(i);
        }
        return texture(images.length - 1);
    }

    /**
     * gets texture id from texture array
     * 2.0.32 - This method now uploads the buffer into texture
     * @param index image index
     * @return texture id usable on OpenGL
     */
    public int texture(int index) {
        this.setupGL();
        if (index == lastIndex)
            return texture;

        // BIND PBO
        GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pboId);

        // COPY TO PBO
        pbo = GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_WRITE_ONLY, this.width * this.height * 4L, pbo);
        RenderAPI.putImageByteBuffer(pbo, images[index]);
        GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);

        // PARAMS
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, 0);

        // UNBIND PBO
        GL21.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);

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
    }
    /**
     * This method drain buffers and release OpenGL textures
     */
    public void release() {
        GL11.glDeleteTextures(texture);
        GL21.glDeleteBuffers(pboId);
        flush();
    }
}
