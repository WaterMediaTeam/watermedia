package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.GifDecoder;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class RenderablePicture {
    public static final Marker IT = MarkerFactory.getMarker(RenderablePicture.class.getSimpleName());

    public final int width;
    public final int height;
    public final int[] textures;
    public final long[] delay;
    public final long duration;
    public final BufferedImage image;
    public final String error;
    public GifDecoder decoder;
    private int uses = 0;

    public int remaining;

    public RenderablePicture() {
        this("Invalid picture");
    }

    public RenderablePicture(String error) {
        this.width = this.height = -1;
        this.textures = new int[0];
        this.delay = new long[0];
        this.duration = -1L;
        this.image = null;
        this.decoder = null;
        this.uses = -1;
        this.error = error;
    }

    public RenderablePicture(@NotNull BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.textures = new int[] { -1 };
        this.delay = new long[] { 0 };
        this.duration = 0;
        this.decoder = null;
        this.image = image;
        this.error = null;
    }

    public RenderablePicture(@NotNull GifDecoder decoder) {
        Dimension frameSize = decoder.getFrameSize();
        this.width = (int) frameSize.getWidth();
        this.height = (int) frameSize.getHeight();
        this.textures = new int[decoder.getFrameCount()];
        this.delay = new long[decoder.getFrameCount()];

        this.decoder = decoder;
        this.image = null;
        this.remaining = decoder.getFrameCount();
        long time = 0;
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            textures[i] = -1;
            delay[i] = time;
            time += decoder.getDelay(i);
        }

        this.duration = time;
        this.error = null;
    }

    public int genTexture(long time) {
        if (textures == null) return -1;
        if (textures.length == 1) return genTexture(0);
        int last = genTexture(0);
        for (int i = 1; i < delay.length; i++) {
            if (delay[i] > time) break;
            last = genTexture(i);
        }
        return last;
    }

    /**
     *
     * @param index texture index
     * @return OpenGL texture
     */
    public int genTexture(int index) {
        if (textures[index] == -1 && decoder != null) {
            textures[index] = WaterMediaAPI.preRender(decoder.getFrame(index), width, height);
            remaining--;
            if (remaining <= 0) decoder = null;
        }
        return textures[index];
    }

    public boolean isValid() {
        synchronized (this) {
            return uses != -2 && uses != -1;
        }
    }

    public boolean isVideo() {
        synchronized (this) {
            return uses == -1;
        }
    }

    public boolean isUsed() {
        synchronized (this) {
            return uses > 0 || uses == -1;
        }
    }

    public RenderablePicture use() {
        synchronized (this) {
            if (uses >= 0) uses++;
            else throw new UnsupportedOperationException("You tried to use an invalid picture");
        }
        return this;
    }

    public RenderablePicture deuse() {
        synchronized (this) {
            if (uses >= 0) uses--;
            else throw new UnsupportedOperationException("You tried to deuse an invalid picture");
        }
        return this;
    }

    /**
     * This method just drain buffers but not releases OpenGL texture
     */
    void flush() {
        synchronized (this) {
            if (uses > 0) LOGGER.warn(IT, "## CRITICAL: FLUSHED RENDERABLE PICTURE WITH CURRENT USAGES");
            if (image != null) image.flush();
            if (decoder != null) {
                for (int i = 0; i < decoder.getFrameCount(); i++) {
                    decoder.getFrame(i).flush();
                }
            }
        }
    }

    /**
     * This method drain buffers and releases OpenGL textures
     */
    void release() {
        for (int tex: textures) if (tex != -1) GL11.glDeleteTextures(tex);
        flush();
    }
}
