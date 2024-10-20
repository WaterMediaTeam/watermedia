package org.watermedia.videolan4j.player.embedded.videosurface.callback;

import org.watermedia.videolan4j.player.embedded.videosurface.CallbackVideoSurface;

// WATERMeDIA PATCH
public interface SimpleBufferFormatCallback {
    /**
     * Returns a {@link BufferFormat} instance specifying how the {@link CallbackVideoSurface} should structure its
     * internal buffers.
     * <p>
     * Note that it is possible that some versions of VLC provide the wrong value for the sourceHeight parameter, and
     * more than that it might invoke this callback multiple times with different values for the sourceHeight. Your own
     * callback implementation may need to mitigate this (e.g. by ignoring the sourceHeight changes on subsequent
     * invocations of your callback).
     *
     * @param sourceWidth  video width
     * @param sourceHeight video height
     * @return buffer format, must never be <code>null</code>
     */
    BufferFormat getBufferFormat(int sourceWidth, int sourceHeight);
}