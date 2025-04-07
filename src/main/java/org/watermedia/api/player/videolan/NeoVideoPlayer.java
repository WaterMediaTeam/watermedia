package org.watermedia.api.player.videolan;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.binding.internal.*;
import org.watermedia.videolan4j.binding.lib.Kernel32;
import org.watermedia.videolan4j.binding.lib.LibC;
import org.watermedia.videolan4j.binding.lib.size_t;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormat;
import org.watermedia.videolan4j.tools.Buffers;
import org.watermedia.videolan4j.tools.Chroma;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

class NeoVideoPlayer extends NeoBasePlayer implements libvlc_video_format_cb, libvlc_video_cleanup_cb, libvlc_lock_callback_t, libvlc_unlock_callback_t, libvlc_display_callback_t {

    private final BufferFormat bufferFormat;
    private final Semaphore semaphore;
    private ByteBuffer[] nativeBuffers;
    private Pointer[] pointers;
    private boolean update;

    public NeoVideoPlayer(MediaPlayerFactory factory, Executor renderExecutor) {
        super(factory);
        this.bufferFormat = null;
        this.semaphore = new Semaphore(1);
    }

    public void preRender() {

    }

    /* VLC CALLBACKS */

    @Override
    public void display(Pointer pointer, Pointer pointer1) {
        semaphore.release();
        this.update = true;
    }

    @Override
    public Pointer lock(Pointer opaque, PointerByReference planes) {
        planes.getPointer().write(0, pointers, 0, pointers.length);
        semaphore.acquireUninterruptibly();
        return null;
    }

    @Override
    public void unlock(Pointer pointer, Pointer pointer1, Pointer pointer2) {

    }

    @Override
    public int format(PointerByReference opaque, PointerByReference chroma, IntByReference width, IntByReference height, PointerByReference pitches, PointerByReference lines) {
        final int w = width.getValue();
        final int h = height.getValue();
        final byte[] chromaBytes = bufferFormat.getChroma().canonical().getBytes();
        final int[] pitchValues = bufferFormat.getChroma().getPitches(w);
        final int[] lineValues = bufferFormat.getChroma().getLines(h);
        chroma.getPointer().write(0, chromaBytes, 0, Math.min(chromaBytes.length, 4));
        pitches.getPointer().write(0, pitchValues, 0, pitchValues.length);
        lines.getPointer().write(0, lineValues, 0, lineValues.length);

        nativeBuffers = new ByteBuffer[pitchValues.length];
        pointers = new Pointer[pitchValues.length];
        for (int i = 0; i < pitchValues.length; i++) {
            final ByteBuffer buffer = Buffers.alloc(pitchValues[i] * lineValues[i]);
            // ALLOCATE
            if (!Buffers.isAligned(Buffers.address(buffer))) {
            }
            // STORE
            nativeBuffers[i] = buffer;
            pointers[i] = Pointer.createConstant(Buffers.address(buffer));
            if (!Platform.isWindows()) {
                LibC.INSTANCE.mlock(pointers[i], new NativeLong(buffer.capacity()));
            } else {
                Kernel32.INSTANCE.VirtualLock(pointers[i], new size_t(buffer.capacity()));
            }
        }

        return nativeBuffers.length;
    }

    @Override
    public void cleanup(Pointer pointer) {
        if (nativeBuffers != null) {
            for (int i = 0; i < nativeBuffers.length; i++) {
                if (!Platform.isWindows()) {
                    LibC.INSTANCE.munlock(pointers[i], new NativeLong(nativeBuffers[i].capacity()));
                } else {
                    Kernel32.INSTANCE.VirtualUnlock(pointers[i], new size_t(nativeBuffers[i].capacity()));
                }
            }
            for(ByteBuffer buffer: nativeBuffers) {
                Buffers.dealloc(buffer);
            }
            nativeBuffers = null;
            pointers = null;
        }
    }
}
