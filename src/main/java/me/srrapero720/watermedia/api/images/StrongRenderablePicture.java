package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.external.GifDecoder;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

/**
 * This was intended to be used with local resources (no url) and dummy for videos (url)
 */
public class StrongRenderablePicture extends RenderablePicture {
    public StrongRenderablePicture(@NotNull BufferedImage image) { super("", image); }
    public StrongRenderablePicture(@NotNull GifDecoder decoder) { super("", decoder); }
    public StrongRenderablePicture(String url) { super(url); }


    @Override
    public boolean isValid() { return true; }

    @Override
    public boolean isVideo() { return super.isVideo(); }

    @Override
    public boolean isUsed() { return true; }

    @Override
    public RenderablePicture use() { return this; }

    @Override
    public RenderablePicture deuse() { return this; }

    @Override
    void flush() {}

    @Override
    void release() {}
}
