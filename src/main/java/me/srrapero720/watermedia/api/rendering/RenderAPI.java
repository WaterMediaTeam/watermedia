package me.srrapero720.watermedia.api.rendering;

import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.api.rendering.memory.MemoryAlloc;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.nio.*;

/**
 * RenderApi is a tool class for OpenGL rendering compatible with all minecraft versions
 */
public class RenderAPI {

    /**
     * Creates a DirectByteBuffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * <p>In case class was missing uses instead {@link java.nio.DirectByteBuffer#allocateDirect(int) DirectByteBuffer#allocateDirect(int)}</p>
     * @param size size of the buffer
     * @return DirectByteBuffer
     */
    public static ByteBuffer createByteBuffer(int size) {
        try {
            return MemoryAlloc.create(size);
        } catch (Throwable t) {
            return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        }
    }

    /**
     * Resizes direct buffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * <p>In case class was missing causes a {@link NotImplementedException}</p>
     * @param buffer buffer to be resized
     * @param newSize new size of the buffer
     * @return resized DirectByteBuffer
     */
    public static ByteBuffer resizeByteBuffer(ByteBuffer buffer, int newSize) {
        try {
            return MemoryAlloc.resize(buffer, newSize);
        } catch (Throwable t) {
            throw new NoSuchMethodError("resizeByteBuffer is not available on LWJGL 2.x");
        }
    }

    /**
     * Creates a PBO and returns the ByteBuffer
     * TODO: enhance doc
     * @return the Buffer
     */
    public static ByteBuffer createMapBuffer(int bufferSize) {
        try {
            return GL15.glMapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, GL15.GL_READ_WRITE, bufferSize, null);
        } finally {
            GL15.glUnmapBuffer(GL21.GL_PIXEL_UNPACK_BUFFER);
        }
    }

    public static ByteBuffer createImageByteBuffer(BufferedImage image, int startX, int startY) {
        int width = image.getWidth();
        int height = image.getHeight();
        Raster raster = image.getRaster();

        ByteBuffer byteBuffer = createByteBuffer(width * height * 4);
        byteBuffer.clear();
        switch (raster.getTransferType()) {
            case DataBuffer.TYPE_BYTE: {
                byte[] data = new byte[width * raster.getNumDataElements()];
                for (int i = startY; i < startY + height; i++) {
                    raster.getDataElements(startX, i, width, 1, data);
                    byteBuffer.put(data);
                }
            }
            break;
            case DataBuffer.TYPE_SHORT:
            case DataBuffer.TYPE_USHORT: {
                ShortBuffer buffer = byteBuffer.asShortBuffer();
                short[] data = new short[width * raster.getNumDataElements()];
                for (int i = startY; i < startY + height; i++) {
                    raster.getDataElements(startX, i, width, 1, data);
                    buffer.put(data);
                }
            }
            break;
            case DataBuffer.TYPE_INT: {
                IntBuffer buffer = byteBuffer.asIntBuffer();
                int[] data = new int[width * raster.getNumDataElements()];
                for (int i = startY; i < startY + height; i++) {
                    raster.getDataElements(startX, i, width, 1, data);
                    buffer.put(data);
                }
            }
            break;
            case DataBuffer.TYPE_FLOAT: {
                FloatBuffer buffer = byteBuffer.asFloatBuffer();
                float[] data = new float[width * raster.getNumDataElements()];
                for (int i = startY; i < startY + height; i++) {
                    raster.getDataElements(startX, i, width, 1, data);
                    buffer.put(data);
                }
            }
            break;
            case DataBuffer.TYPE_DOUBLE: {
                DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
                double[] data = new double[width * raster.getNumDataElements()];
                for (int i = startY; i < startY + height; i++) {
                    raster.getDataElements(startX, i, width, 1, data);
                    buffer.put(data);
                }
            }
            break;
        }
        byteBuffer.flip();
        return byteBuffer;
    }

    public static ByteBuffer createImageRGBByteBuffer(BufferedImage image, int startX, int startY) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(startX, startY, width, height, pixels, 0, width);
        boolean alpha = false;

        if (image.getColorModel().hasAlpha()) for (int pixel : pixels)
            if ((pixel >> 24 & 0xFF) < 0xFF) {
                alpha = true;
                break;
            }

        int bytesPerPixel = alpha ? 4 : 3;
        ByteBuffer buffer = createMapBuffer(width * height * bytesPerPixel);
        for (int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
            buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green
            buffer.put((byte) (pixel & 0xFF)); // Blue
            if (alpha) buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
        }
        ((Buffer) buffer).flip();
        return buffer;
    }

    /**
     * Creates a new texture id based on a {@link BufferedImage} instance
     * (used internally by {@link ImageRenderer}
     * @param image image to process
     * @param width buffer width (can be image width)
     * @param height buffer height (can be image height)
     * @return texture id for OpenGL
     */
    public static int applyBuffer(BufferedImage image, int width, int height) {
        ByteBuffer buffer = createImageByteBuffer(image, 0, 0);
        boolean alpha = image.getColorModel().hasAlpha();

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        if (!alpha) GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, GL11.GL_ONE);

        // prevents random crash; when values are too high it causes a jvm crash, caused weird behavior when game is paused
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        //Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, alpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, width, height, 0, alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID, so we can bind it later again
        return textureID;
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param videoBuffer IntBuffer to be processed
     * @param glTexture texture ID from OpenGL
     * @param videoWidth buffer width
     * @param videoHeight buffer height
     * @param firstFrame if was the first frame
     */
    public static void applyBuffer(IntBuffer videoBuffer, int glTexture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param videoBuffer ByteBuffer to be processed
     * @param glTexture texture ID from OpenGL
     * @param videoWidth buffer width
     * @param videoHeight buffer height
     * @param firstFrame if was the first frame
     */
    public static void applyBuffer(ByteBuffer videoBuffer, int glTexture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexture);
        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
    }
}