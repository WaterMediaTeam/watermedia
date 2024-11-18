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

package org.watermedia.videolan4j.factory;

import com.sun.jna.StringArray;
import org.watermedia.videolan4j.discovery.NativeDiscovery;
import org.watermedia.videolan4j.support.eventmanager.TaskExecutor;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;

import java.net.URI;
import java.util.Collection;

/**
 * Factory for creating media player instances and associated components.
 * <p>
 * When using VLC options/arguments to initialise the factory, generally any options that enable/disable modules (e.g.
 * video/audio filters) must be set via the factory instance and not when invoking
 * {@link org.watermedia.videolan4j.player.base.MediaApi#play(URI, String...)}. However, the module-specific
 * options <em>may</em> be able to be passed as media options and be effective via that play call.
 * <p>
 * The factory will attempt to automatically discover the location of the required LibVLC native library, so it should
 * just work by default (at least for the most common/likely environment configurations). If you have other requirements
 * for the native library discovery mechanism, you can pass in your own implementation of {@link NativeDiscovery} when
 * you create the factory.
 * <p>
 * You should explicitly {@link #release()} the factory when your application terminates to properly clean up native
 * resources.
 * <p>
 * The factory also provides access to the native libvlc Logger and other resources such as the list of audio outputs,
 * and the list of available audio and video filters.
 * <p>
 * You <em>must</em> make sure you keep a hard reference to the components created by this factory to prevent them from
 * going out of scope and being garbage-collected. If you allow one of these components to go out of scope, then
 * unpredictable behaviour will occur (such as events no longer seeming to fire) even though the video playback may
 * continue (since that happens via native code). You will also likely suffer fatal JVM crashes.
 * <p>
 * It is <em>always</em> a better strategy to <em>reuse</em> media player instances, rather than repeatedly creating
 * and destroying them.
 */
public class MediaPlayerFactory {

    /**
     * Native library instance.
     */
    protected final libvlc_instance_t libvlcInstance;

    /**
     * Single-threaded service to execute tasks that need to be off-loaded from a native callback thread.
     * <p>
     * See {@link #submit(Runnable)}.
     */
    private final TaskExecutor executor = new TaskExecutor();

    private final ApplicationApi     applicationApi;
    private final AudioApi           audioApi;
    private final DialogsApi         dialogsApi;
    private final MediaDiscovererApi mediaDiscovererApi;
    private final EqualizerApi       equalizerApi;
    private final MediaPlayerApi     mediaPlayerApi;
    private final MediaApi           mediaApi;
    private final RendererApi        rendererApi;
    private final VideoSurfaceApi    videoSurfaceApi;

    /**
     * Create a new media player factory.
     *
     * @param libvlcArgs array of options/arguments to pass to LibVLC for initialisation of the native library
     * @throws NativeLibraryMappingException if one or more of the declared method bindings in {@link LibVlc} could not be found in the native library that was loaded
     */
    public MediaPlayerFactory(String... libvlcArgs) {

        try {
            if (!NativeDiscovery.start())
                throw new IllegalStateException("VideoLAN is not properly discovered");
        } catch (NoClassDefFoundError e) {
            throw new NativeLibraryMappingException("Failed to properly initialise the native library", e);
        }

        this.libvlcInstance = newLibVlcInstance(libvlcArgs != null ? libvlcArgs : new String[0]);

        this.applicationApi     = new ApplicationApi    (this);
        this.audioApi           = new AudioApi          (this);
        this.dialogsApi         = new DialogsApi        (this);
        this.mediaDiscovererApi = new MediaDiscovererApi(this);
        this.equalizerApi       = new EqualizerApi      (this);
        this.mediaPlayerApi     = new MediaPlayerApi    (this);
        this.mediaApi           = new MediaApi          (this);
        this.rendererApi        = new RendererApi       (this);
        this.videoSurfaceApi    = new VideoSurfaceApi   (this);
    }

    /**
     * Create a new media player factory.
     * <p>
     * If you do not want to use native discovery, pass <code>null</code> for the <code>discovery</code> parameter.
     *
     * @param libvlcArgs collection of options/arguments to pass to LibVLC for initialisation of the native library
     */
    public MediaPlayerFactory(Collection<String> libvlcArgs) {
        this(libvlcArgs.toArray(new String[0]));
    }

    /**
     * Get a new LibVLC instance.
     *
     * @param libvlcArgs native library initialisation arguments/options
     * @return native library instance
     */
    private libvlc_instance_t newLibVlcInstance(String... libvlcArgs) {
        libvlc_instance_t result = LibVlc.libvlc_new(libvlcArgs.length, new StringArray(libvlcArgs));
        if (result != null) {
            return result;
        } else {
            throw new RuntimeException("Failed to get a new native library instance");
        }
    }

    public final ApplicationApi application() {
        return applicationApi;
    }

    public final AudioApi audio() {
        return audioApi;
    }

    public final DialogsApi dialogs() {
        return dialogsApi;
    }

    public final MediaDiscovererApi mediaDiscoverers() {
        return mediaDiscovererApi;
    }

    public final EqualizerApi equalizer() {
        return equalizerApi;
    }

    public final MediaPlayerApi mediaPlayers() {
        return mediaPlayerApi;
    }

    public final MediaApi media() {
        return mediaApi;
    }

    public final RendererApi renderers() {
        return rendererApi;
    }

    public final VideoSurfaceApi videoSurfaces() {
        return videoSurfaceApi;
    }

    /**
     * Submit a task for asynchronous execution.
     * <p>
     * This is useful in particular for event handling code as native events are generated on a native event callback
     * thread and it is not allowed to call back into LibVLC from this callback thread. If you do, either the call will
     * be ineffective, strange behaviour will happen, or a fatal JVM crash may occur.
     * <p>
     * To mitigate this, those tasks can be offloaded from the native thread, serialised and executed using this method.
     *
     * @param r task to execute
     */
    public final void submit(Runnable r) {
        executor.submit(r);
    }

    /**
     * Release all native resources associated with this factory.
     * <p>
     * The factory must <em>not</em> be used again after it has been released.
     */
    public final void release() {
        executor.release();

        onBeforeRelease();

        applicationApi    .release();
        audioApi          .release();
        dialogsApi        .release();
        mediaDiscovererApi.release();
        equalizerApi      .release();
        mediaPlayerApi    .release();
        mediaApi          .release();
        rendererApi       .release();
        videoSurfaceApi   .release();

        LibVlc.libvlc_release(this.libvlcInstance);

        onAfterRelease();
    }

    /**
     * Template method invoked immediately prior to the factory being released.
     * <p>
     * A factory sub-class can override this to perform its own clean-up before the factory goes away.
     */
    protected void onBeforeRelease() {
    }

    /**
     * Template method invoked immediately after the factory has been released.
     * <p>
     * A factory subclass can override this to perform its own clean-up after the factory goes away.
     */
    protected void onAfterRelease() {
    }

}
