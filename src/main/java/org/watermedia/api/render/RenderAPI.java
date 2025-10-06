package org.watermedia.api.render;

import org.lwjgl.system.MemoryUtil;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.*;
import org.watermedia.videolan4j.VideoLan4J;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * RenderApi is a tool class for OpenGL rendering compatible with lwjgl 3.x
 */
public class RenderAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(RenderAPI.class.getSimpleName());
    public static final int NONE = 0;
    public static final long NULL = 0L;
    public static final boolean ADVANCED_LWJGL = true; // LWJGL 2.9 doesn't have MemoryAllocator

    /**
     * Creates a DirectByteBuffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * <p>In case class was missing uses instead {@link ByteBuffer#allocateDirect(int) DirectByteBuffer#allocateDirect(int)}</p>
     * @param size size of the buffer
     *
     * @return DirectByteBuffer
     */
    @Deprecated(forRemoval = true)
    public static ByteBuffer createByteBuffer(int size) {
        return createByteBuffer(1, size);
    }

    public static ByteBuffer createByteBuffer(int alignment, int size) {
        if (ADVANCED_LWJGL) {
            return MemoryUtil.memAlignedAlloc(alignment, size);
        } else {
            return ByteBuffer.allocateDirect(size); // Fallback to old way
        }
    }

    /**
     * Resizes direct buffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * @param buffer buffer to be resized
     * @param newSize new size of the buffer
     * @return resized DirectByteBuffer
     */
    public static ByteBuffer resizeByteBuffer(ByteBuffer buffer, int newSize) {
        if (ADVANCED_LWJGL) {
            MemoryUtil.MemoryAllocator allocator = MemoryUtil.getAllocator(false);
            long address = allocator.realloc(MemoryUtil.memAddress0((Buffer) buffer), newSize);
            if (address == NULL)
                throw new OutOfMemoryError("Insufficient memory to reallocate " + newSize + " bytes");

            return MemoryUtil.memByteBuffer(address, newSize);
        } else {
            return ByteBuffer.allocateDirect(newSize).put(buffer);
        }
    }

    /**
     * Deletes the direct buffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * <p>In case class was missing fallbacks into unsafe cleaner</p>
     * @param buffer buffer to free
     */
    public static void freeByteBuffer(ByteBuffer buffer) {
        if (ADVANCED_LWJGL) {
            MemoryUtil.memAlignedFree(buffer);
        }
    }

    /**
     * Converts the BufferedImage into a different format
     *
     * @param originalImage original image in any other format
     * @return converted image to ARGB format
     */
    public static BufferedImage formatToArgb(BufferedImage originalImage) {
        // If image type is already good then no conversion needed, so we use the original image.
        if(originalImage.getType() == BufferedImage.TYPE_INT_ARGB) return originalImage;

        // Convert the image to the expected format.
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static ByteBuffer[] getImageBuffer(BufferedImage[] images) {
        ByteBuffer[] buffers = new ByteBuffer[images.length];
        for (int i = 0; i < images.length; i++) {
            buffers[i] = getImageBuffer(images[i]);
        }
        return buffers;
    }

    /**
     * Converts the format and stores the pixels into a ByteBuffer ready to be used by OpenGL
     * @param image Image to convert
     * @return ByteBuffer of the image
     */
    public static ByteBuffer getImageBuffer(BufferedImage image) {
        image = formatToArgb(image);
        int[] pixels = ((DataBufferInt) formatToArgb(image).getRaster().getDataBuffer()).getData();

        ByteBuffer buffer = createByteBuffer(image.getWidth() * image.getHeight() * 4);
        buffer.asIntBuffer().put(pixels);

        ((Buffer) buffer).flip();
        return buffer;
    }

    public static int createTexture() {
        final int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        // Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Unbind
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);  // Unbind

        return id;
    }


    /**
     * Uploads the reversed buffer data to the gl texture
     * @param buffer ByteBuffer to be processed
     * @param texture texture ID from OpenGL
     * @param format the texel data format. One of:<br><table><tr><td>{@link GL11C#GL_RED RED}</td><td>{@link GL11C#GL_GREEN GREEN}</td><td>{@link GL11C#GL_BLUE BLUE}</td><td>{@link GL11C#GL_ALPHA ALPHA}</td><td>{@link GL30#GL_RG RG}</td><td>{@link GL11C#GL_RGB RGB}</td><td>{@link GL11C#GL_RGBA RGBA}</td><td>{@link GL12#GL_BGR BGR}</td></tr><tr><td>{@link GL12#GL_BGRA BGRA}</td><td>{@link GL30#GL_RED_INTEGER RED_INTEGER}</td><td>{@link GL30#GL_GREEN_INTEGER GREEN_INTEGER}</td><td>{@link GL30#GL_BLUE_INTEGER BLUE_INTEGER}</td><td>{@link GL30#GL_ALPHA_INTEGER ALPHA_INTEGER}</td><td>{@link GL30#GL_RG_INTEGER RG_INTEGER}</td><td>{@link GL30#GL_RGB_INTEGER RGB_INTEGER}</td><td>{@link GL30#GL_RGBA_INTEGER RGBA_INTEGER}</td></tr><tr><td>{@link GL30#GL_BGR_INTEGER BGR_INTEGER}</td><td>{@link GL30#GL_BGRA_INTEGER BGRA_INTEGER}</td><td>{@link GL11C#GL_STENCIL_INDEX STENCIL_INDEX}</td><td>{@link GL11C#GL_DEPTH_COMPONENT DEPTH_COMPONENT}</td><td>{@link GL30#GL_DEPTH_STENCIL DEPTH_STENCIL}</td></tr></table>
     * @param width buffer image width
     * @param height buffer image height
     * @param first when is the first frame first we have to initialize it
     */
    public static void uploadBuffer(ByteBuffer buffer, int texture, int format, int width, int height, boolean first) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        if (first)
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, format, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        else
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, format, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
    }


    /**
     * Reads the texture data into a new bytebuffer
     * @param texture opengl textur eid
     * @param width image width
     * @param height image height
     * @return image data
     */
    public static ByteBuffer downloadBuffer(int texture, int width, int height) {
        ByteBuffer buffer = createByteBuffer(width * height * 4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glGetTexImage(texture, 0, GL12.GL_RGBA8, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        return buffer;
    }

    public static void deleteTexture(int texture) {
        GL11.glDeleteTextures(texture);
    }

    public static void deleteTexture(int[] textures) {
        GL11.glDeleteTextures(textures);
    }

    public static void bindTexture(int id) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public static void bindTexture() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NONE);
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        // REPLACE JAVA WAY FOR LWJGL WAY
        if (ADVANCED_LWJGL) {
            VideoLan4J.setBufferAllocator(RenderAPI::createByteBuffer);
            VideoLan4J.setBufferDeallocator(RenderAPI::freeByteBuffer);
        }
    }

    @Override
    public void release() {

    }
}