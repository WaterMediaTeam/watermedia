package org.watermedia.api;

import org.lwjgl.system.MemoryUtil;
import org.watermedia.WaterMedia;
import org.watermedia.videolan4j.VideoLan4J;

import java.nio.ByteBuffer;

public class MemoryAPI extends WaterMediaAPI {
    private static MemoryUtil.MemoryAllocator ALLOCATOR;

    /**
     * Creates a DirectByteBuffer unsafe using {@link MemoryUtil.MemoryAllocator MemoryAllocator}
     * @param size size of the buffer
     * @return native buffer
     */
    public static ByteBuffer allocate(int size) {
        if (ALLOCATOR == null)
            throw new IllegalStateException("MemoryAPI is uninitialized");

        long i = ALLOCATOR.malloc(size);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, size);
        }
    }

    public static ByteBuffer resize(ByteBuffer buffer, int size) {
        if (ALLOCATOR == null)
            throw new IllegalStateException("MemoryAPI is uninitialized");
        if (buffer == null)
            throw new NullPointerException("ByteBuffer is null");
        if (!buffer.isDirect())
            throw new UnsupportedOperationException("ByteBuffer must be direct and allocated by the MemoryAPI");

        long i = ALLOCATOR.realloc(MemoryUtil.memAddress0(buffer), size);
        if (i == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + buffer.capacity() + " bytes to " + size + " bytes");
        } else {
            return MemoryUtil.memByteBuffer(i, size);
        }
    }

    public static void deallocate(ByteBuffer... buffers) {
        if (ALLOCATOR == null)
            throw new IllegalStateException("MemoryAPI is uninitialized");

        if (buffers == null) return;
        for (ByteBuffer b: buffers) {
            if (!b.isDirect()) continue;
            ALLOCATOR.free(MemoryUtil.memAddress0(b));
        }
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        return ALLOCATOR == null;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception {
        ALLOCATOR = MemoryUtil.getAllocator(false);
        // REPLACE JAVA WAY FOR LWJGL WAY
        VideoLan4J.setBufferAllocator(MemoryAPI::allocate);
        VideoLan4J.setBufferDeallocator(MemoryAPI::deallocate);
    }

    @Override
    public void release() {
        ALLOCATOR = null;
    }
}
