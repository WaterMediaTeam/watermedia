package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.Util;
import me.srrapero720.watermedia.api.external.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RenderablePicture {
    public final int width;
    public final int height;
    public final int[] textures;
    public final long[] delay;
    public final long duration;
    public final BufferedImage image;
    public GifDecoder decoder;

    public int remaining;

    public RenderablePicture(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.textures = new int[] { Util.preRender(image, width, height) };
        this.delay = new long[] { 0 };
        this.duration = 0;
        this.decoder = null;
        this.image = image;
    }

    public RenderablePicture(GifDecoder decoder) {
        Dimension frameSize = decoder.getFrameSize();
        width = (int) frameSize.getWidth();
        height = (int) frameSize.getHeight();
        textures = new int[decoder.getFrameCount()];
        delay = new long[decoder.getFrameCount()];

        this.decoder = decoder;
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
        if (textures[index] == -1 && decoder != null) {
            textures[index] = Util.preRender(decoder.getFrame(index), width, height);
            remaining--;
            if (remaining <= 0) decoder = null;
        }
        return textures[index];
    }
}
