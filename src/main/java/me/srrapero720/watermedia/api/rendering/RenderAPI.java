package me.srrapero720.watermedia.api.rendering;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.api.rendering.memory.MemoryAlloc;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * RenderApi is a tool class for OpenGL rendering compatible with all minecraft versions
 */
public class RenderAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(RenderAPI.class.getSimpleName());

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
     * Deletes the direct buffer unsafe using {@link org.lwjgl.system.MemoryUtil.MemoryAllocator MemoryAllocator}
     *
     * <p>In case class was missing fallbacks into unsafe cleaner</p>
     * @param buffer buffer to free
     */
    public static void freeByteBuffer(ByteBuffer buffer) {
        try {
            MemoryAlloc.free(buffer);
        } catch (Throwable t) {
            if(!buffer.isDirect()) return;
            try {
//                DirectBuffer db = (DirectBuffer) buffer;
//                if (db.attachment() != null)
//                    throw new IllegalArgumentException("duplicate or slice");
//
//                Cleaner cleaner = db.cleaner();
//                if (cleaner != null) {
//                    cleaner.clean();
//                }
            } catch(Throwable ex) {
                LOGGER.error(IT, "Failed to delete DirectByteBuffer");
            }
        }
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


    /**
     * Created by CreativeMD
     * Creates a new texture id based on a {@link BufferedImage} instance
     * (used internally by {@link ImageRenderer}
     * @param image image to process
     * @param width buffer width (can be image width)
     * @param height buffer height (can be image height)
     * @return texture id for OpenGL
     */
    public static int applyBuffer(BufferedImage image, int width, int height) {
        image = convertImageFormat(image);
        int[] pixels = ((DataBufferInt) convertImageFormat(image).getRaster().getDataBuffer()).getData();

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        buffer.asIntBuffer().put(pixels);

        /*
         * FLIP method changes what class type returns in new JAVA versions, in runtime causes a JVM crash by that
         * THIS EXECUTES {@link ByteBuffer#flip }
         */
        ((Buffer) buffer).flip();

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        // prevents random crash; when values are too high it causes a jvm crash, caused weird behavior when game is paused
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        //Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);

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

    @Override
    public Priority priority() {
        return Priority.LOW;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return false;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}