package org.watermedia.api.render;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

public class RenderAPI {
    private static final Marker IT = MarkerManager.getMarker(RenderAPI.class.getSimpleName());
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);


    /**
     * Creates a DirectByteBuffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     * @param size size of the buffer
     * @return native buffer
     */
    public static ByteBuffer createBuffer(int size) {
        long i = ALLOCATOR.malloc(size);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, size);
        }
    }

    public static ByteBuffer resizeBuffer(ByteBuffer buffer, int size) {
        long i = ALLOCATOR.realloc(MemoryUtil.memAddress0(buffer), size);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + buffer.capacity() + " bytes to " + size + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, size);
        }
    }

    public static void releaseBuffer(ByteBuffer buffer) {
        if (buffer == null) return;
        ALLOCATOR.free(MemoryUtil.memAddress0(buffer));
    }

    public static BufferedImage convertImageFormat(BufferedImage originalImage) {
        // If image type is already good then no conversion needed, so we use the original image.
        if(originalImage.getType() == BufferedImage.TYPE_INT_ARGB) return originalImage;

        // Convert the image to the expected format.
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static ByteBuffer[] getRawImageBuffer(BufferedImage[] images) {
        ByteBuffer[] buffers = new ByteBuffer[images.length];
        for (int i = 0; i < images.length; i++) {
            buffers[i] = getRawImageBuffer(images[i]);
        }
        return buffers;
    }

    /**
     * Converts the format and stores the pixels into a ByteBuffer ready to be used by OpenGL
     * @param image Image to convert
     * @return ByteBuffer of the image
     */
    public static ByteBuffer getRawImageBuffer(BufferedImage image) {
        image = convertImageFormat(image);
        int[] pixels = ((DataBufferInt) convertImageFormat(image).getRaster().getDataBuffer()).getData();

        ByteBuffer buffer = createBuffer(image.getWidth() * image.getHeight() * 4);
        buffer.asIntBuffer().put(pixels);
        buffer.flip();
        return buffer;
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param buffer ByteBuffer to be processed
     * @param texture texture ID from OpenGL
     * @param videoWidth buffer width
     * @param videoHeight buffer height
     * @param firstFrame if was the first frame
     */
    public static void uploadBuffer(ByteBuffer buffer, int texture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

}
