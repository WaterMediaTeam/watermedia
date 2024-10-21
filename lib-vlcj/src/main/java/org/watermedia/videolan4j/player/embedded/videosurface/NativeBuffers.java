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

package org.watermedia.videolan4j.player.embedded.videosurface;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import org.watermedia.videolan4j.ByteBufferFactory;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;
import org.watermedia.videolan4j.binding.lib.Kernel32;
import org.watermedia.videolan4j.binding.lib.LibC;
import org.watermedia.videolan4j.binding.lib.types.size_t;

import java.nio.ByteBuffer;

final class NativeBuffers {

    private final boolean lockBuffers;

    /**
     * Native memory buffers, one for each plane.
     */
    private ByteBuffer[] nativeBuffers;

    /**
     * Native memory pointers to each byte buffer.
     */
    private Pointer[] pointers;

    public NativeBuffers(boolean lockBuffers) {
        this.lockBuffers = lockBuffers;
    }

    /**
     *
     * Memory must be aligned correctly (on a 32-byte boundary) for the libvlc API functions, this is all taken care of
     * by the {@link ByteBufferFactory}.
     *
     * @param bufferFormat
     * @return
     */
    int allocate(BufferFormat bufferFormat) {
        int planeCount = bufferFormat.getPlaneCount();
        int[] pitchValues = bufferFormat.getPitches();
        int[] lineValues = bufferFormat.getLines();
        nativeBuffers = new ByteBuffer[planeCount];
        pointers = new Pointer[planeCount];
        for (int i = 0; i < planeCount; i ++) {
            ByteBuffer buffer = ByteBufferFactory.alloc(pitchValues[i] * lineValues[i]);
            if (!ByteBufferFactory.isAligned(ByteBufferFactory.address(buffer))) {
                VideoLan4J.LOGGER.warn("Detected an unaligned buffer. this might lead in I/O issues");
            }
            nativeBuffers[i] = buffer;
            pointers[i] = Pointer.createConstant(ByteBufferFactory.address(buffer));
            if (lockBuffers) {
                if (!Platform.isWindows()) {
                    LibC.INSTANCE.mlock(pointers[i], new NativeLong(buffer.capacity()));
                } else {
                    Kernel32.INSTANCE.VirtualLock(pointers[i], new size_t(buffer.capacity()));
                }
            }
        }
        return nativeBuffers.length;
    }

    void free() {
        if (nativeBuffers != null) {
            if (lockBuffers) {
                for (int i = 0; i < nativeBuffers.length; i++) {
                    if (!Platform.isWindows()) {
                        LibC.INSTANCE.munlock(pointers[i], new NativeLong(nativeBuffers[i].capacity()));
                    } else {
                        Kernel32.INSTANCE.VirtualUnlock(pointers[i], new size_t(nativeBuffers[i].capacity()));
                    }
                }
            }
            // WATERMeDIA
            for(ByteBuffer buffer: nativeBuffers) {
                ByteBufferFactory.dealloc(buffer);
            }
            nativeBuffers = null;
            pointers = null;
        }
    }

    ByteBuffer[] buffers() {
        return nativeBuffers;
    }

    Pointer[] pointers() {
        return pointers;
    }

}
