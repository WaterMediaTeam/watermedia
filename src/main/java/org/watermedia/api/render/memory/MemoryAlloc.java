package org.watermedia.api.render.memory;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * @deprecated No replacement
 */
@Deprecated
public class MemoryAlloc {
    private static MemoryUtil.MemoryAllocator ALLOCATOR;

    /**
     * @deprecated No replacement
     */
    @Deprecated
    public static ByteBuffer create(int pSize) {
        if (ALLOCATOR == null) ALLOCATOR = MemoryUtil.getAllocator(false);

        long i = ALLOCATOR.malloc(pSize);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + pSize + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, pSize);
        }
    }

    /**
     * @deprecated No replacement
     */
    @Deprecated
    public static ByteBuffer resize(ByteBuffer pBuffer, int pByteSize) {
        if (ALLOCATOR == null) ALLOCATOR = MemoryUtil.getAllocator(false);

        long i = ALLOCATOR.realloc(MemoryUtil.memAddress0(pBuffer), pByteSize);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + pBuffer.capacity() + " bytes to " + pByteSize + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, pByteSize);
        }
    }

    /**
     * @deprecated No replacement
     */
    @Deprecated
    public static void free(ByteBuffer pBuffer) {
        if (pBuffer == null) return;
        if (ALLOCATOR == null) ALLOCATOR = MemoryUtil.getAllocator(false);

        ALLOCATOR.free(MemoryUtil.memAddress0(pBuffer));
    }
}