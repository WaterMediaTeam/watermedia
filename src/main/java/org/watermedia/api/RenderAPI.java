package org.watermedia.api;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.watermedia.WaterMedia;
import org.watermedia.videolan4j.VideoLan4J;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

public class RenderAPI extends WaterMediaAPI {

    /**
     * Converts the image on other formats into 4-BYTE RGBA (java ABGR)
     *
     * <p>RenderAPI only works with RGBA buffer format</p>
     *
     * @param oldImage image to convert into the desired format
     * @return converted image to the desired format, same object if already has the right format
     */
    public static BufferedImage convertFormat(BufferedImage oldImage) {
        if (oldImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) return oldImage; // no conversion needed when is what we want

        // Convert the image to the expected format.
        final var newImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = newImage.getGraphics();
        g.drawImage(oldImage, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static ByteBuffer[] getByteBuffers(BufferedImage[] images) {
        ByteBuffer[] buffers = new ByteBuffer[images.length];
        for (int i = 0; i < images.length; i++) {
            buffers[i] = getByteBuffer(images[i]);
        }
        return buffers;
    }

    /**
     * Converts the format and stores the pixels into a ByteBuffer ready to be used in OpenGL
     * @param image Image to convert
     * @return ByteBuffer of the image
     */
    public static ByteBuffer getByteBuffer(BufferedImage image) {
        image = convertFormat(image);
        byte[] pixels = ((DataBufferByte) convertFormat(image).getRaster().getDataBuffer()).getData();

        ByteBuffer buffer = MemoryAPI.allocate(image.getWidth() * image.getHeight() * 4);
        buffer.put(pixels);
        buffer.flip();
        return buffer;
    }

    public static int genTexture() {
        int texture = GL11.glGenTextures();

        // Bind
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering (no dark textures)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // Unbind
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return texture;
    }

    public static void delTexture(int texture) {
        GL11.glDeleteTextures(texture);
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param buffer ByteBuffer to be processed
     * @param texture texture ID from OpenGL
     * @param width buffer width
     * @param height buffer height
     * @param firstFrame if was the first frame
     */
    public static void uploadBuffer(ByteBuffer buffer, int texture, int width, int height, boolean firstFrame) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        if (firstFrame)
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        else
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    /**
     * Downloads a buffer from the GPU
     * @param texture texture ID from OpenGL
     * @param width buffer width
     * @param height buffer height
     * @return allocated buffer with the raw texture data
     */
    public static ByteBuffer downloadBuffer(int texture, int width, int height) {
        ByteBuffer buffer = MemoryAPI.allocate(width * height * 4);
        GL11.glGetTexImage(texture, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        return buffer;
    }

    @Override
    public Priority priority() {
        return Priority.NORMAL;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        return true;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}
