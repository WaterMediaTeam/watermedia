/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package org.watermedia.videolan4j.player.embedded.videosurface.callback;

import org.watermedia.videolan4j.player.embedded.videosurface.CallbackVideoSurface;

import java.nio.ByteBuffer;

/**
 * Callback invoked by the {@link CallbackVideoSurface} when the format of the video changes.
 */
// WATERMeDIA PATCH
// public interface BufferFormatCallback {
public interface BufferFormatCallback extends SimpleBufferFormatCallback {
    /**
     * Invoked when new video buffers have been allocated.
     *
     * @param buffers buffers that were allocated
     */
    void allocatedBuffers(ByteBuffer[] buffers);
}