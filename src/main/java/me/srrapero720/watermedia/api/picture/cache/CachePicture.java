package me.srrapero720.watermedia.api.picture.cache;

import me.srrapero720.watermedia.api.external.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CachePicture {
    public final int width;
    public final int height;
    public final int[] textures;
    public final long[] delay;
    public final long duration;
    public final BufferedImage image;
    public final GifDecoder decoder;

    public int remaining;

    public CachePicture(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.textures = new int[1];
        this.delay = new long[] { 0 };
        this.duration = 0;
        this.decoder = null;
        this.image = image;
    }

    public CachePicture(GifDecoder decoder) {
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
}
