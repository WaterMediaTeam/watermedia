package org.watermedia.videolan4j.player.embedded.videosurface.callback;

import java.nio.ByteBuffer;

public interface BufferCleanupCallback {
    void cleanupBuffers(ByteBuffer[] buffers);
}
