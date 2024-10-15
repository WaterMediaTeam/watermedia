package org.watermedia.api.image;

import me.srrapero720.watermedia.api.rendering.RenderAPI;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public record Image(int width, int height, int[] textures, long[] delay, long duration, ByteBuffer[] images) {


}
