package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.lib720.madgag.gif.fmsware.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageRenderer {
    public final int width;
    public final int height;
    public final int[] textures;
    public final long[] delay;
    public final long duration;
    public final BufferedImage image;
    public GifDecoder decoder;

    public int remaining;

    public ImageRenderer(BufferedImage image) {
        this.image = image;
        if (image == null) throw new NullPointerException();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.textures = new int[] { -1 };
        this.delay = new long[] { 0 };
        this.duration = 0;
        this.decoder = null;
    }

    public ImageRenderer(GifDecoder decoder) {
        this.decoder = decoder;
        if (decoder == null) throw new NullPointerException();

        Dimension frameSize = decoder.getFrameSize();
        width = (int) frameSize.getWidth();
        height = (int) frameSize.getHeight();
        textures = new int[decoder.getFrameCount()];
        delay = new long[decoder.getFrameCount()];

        this.image = null;
        this.remaining = decoder.getFrameCount();
        long time = 0;
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            textures[i] = -1;
            delay[i] = time;
            time += decoder.getDelay(i);
        }

        duration = time;
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

    public int genTexture(int index) {
        if (this.textures[index] == -1) {
            if (decoder != null) {
                this.textures[index] = WaterMediaAPI.gl_genTexture(this.decoder.getFrame(index), width, height);
                if (--this.remaining <= 0) decoder = null;
            } else if (image != null) {
                this.textures[index] = WaterMediaAPI.gl_genTexture(this.image, width, height);
            }
        }
        return textures[index];
    }
}
