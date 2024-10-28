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

package org.watermedia.videolan4j.player.component;

import com.sun.jna.Platform;
import org.watermedia.videolan4j.player.component.callback.CallbackImagePainter;
import org.watermedia.videolan4j.player.component.callback.ScaledCallbackImagePainter;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.*;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.base.MediaPlayer;
import org.watermedia.videolan4j.player.embedded.EmbeddedMediaPlayer;
import org.watermedia.videolan4j.player.embedded.fullscreen.FullScreenStrategy;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.format.RV32BufferFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

/**
 * Implementation of a callback "direct-rendering" media player.
 * <p>
 * This component renders video frames received via native callbacks.
 * <p>
 * The component may be added directly to a user interface layout - this is optional, you can use this component without
 * adding it directly to a user interface, in which case you would simply render the video however you like.
 * <p>
 * When the component is no longer needed, it should be released by invoking the {@link #release()} method.
 */
public class CallbackMediaPlayerComponent extends EmbeddedMediaPlayerComponentBase implements MediaPlayerComponent {

    /**
     * Default factory initialisation arguments.
     */
    static final String[] DEFAULT_FACTORY_ARGUMENTS = MediaPlayerComponentDefaults.EMBEDDED_MEDIA_PLAYER_ARGS;

    /**
     * Flag true if this component created the media player factory, or false if it was supplied by the caller.
     */
    private final boolean ownFactory;

    /**
     * Media player factory.
     */
    protected final MediaPlayerFactory mediaPlayerFactory;

    /**
     * Default render callback implementation, will be <code>null</code> if the client application provides its own
     * render callback.
     */
    private final DefaultRenderCallback defaultRenderCallback;

    /**
     * Painter used to render the video, will be <code>null</code>. if the client application provides its own render
     * callback.
     * <p>
     * Ordinarily set via constructor, but may be changed via {@link #setImagePainter(CallbackImagePainter)}.
     */
    private CallbackImagePainter imagePainter;

    /**
     * Media player.
     */
    private final EmbeddedMediaPlayer mediaPlayer;

    /**
     * Image used to render the video.
     */
    private BufferedImage image;

    /**
     * Construct a callback media player component.
     * <p>
     * This component will provide a reasonable default implementation, but a client application is free to override
     * these defaults with their own implementation.
     * <p>
     * To rely on the defaults and have this component render the video, do not supply a <code>renderCallback</code>.
     * <p>
     * If a client application wishes to perform its own rendering, provide a <code>renderCallback</code>, a
     * <code>BufferFormatCallback</code>, and optionally (but likely) a <code>videoSurfaceComponent</code> if the client
     * application wants the video surface they are rendering in to be incorporated into this component's layout.
     *
     * @param mediaPlayerFactory media player factory
     * @param fullScreenStrategy full screen strategy
     * @param inputEvents keyboard/mouse input event configuration
     * @param lockBuffers <code>true</code> if the native video buffer should be locked; <code>false</code> if not
     * @param imagePainter image painter (video renderer)
     * @param renderCallback render callback
     * @param bufferFormatCallback buffer format callback
     * @param cleanupCallback executed BEFORE buffers got released
     */
    public CallbackMediaPlayerComponent(MediaPlayerFactory mediaPlayerFactory, FullScreenStrategy fullScreenStrategy, InputEvents inputEvents, boolean lockBuffers, CallbackImagePainter imagePainter, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        this.ownFactory = mediaPlayerFactory == null;
        this.mediaPlayerFactory = initMediaPlayerFactory(mediaPlayerFactory);

        validateArguments(imagePainter, renderCallback, bufferFormatCallback, cleanupCallback);

        if (renderCallback == null) {
            this.defaultRenderCallback = new DefaultRenderCallback();
            this.imagePainter          = imagePainter == null ? new ScaledCallbackImagePainter() : imagePainter;
            bufferFormatCallback       = new DefaultBufferFormatCallback();
            renderCallback             = this.defaultRenderCallback;
        } else {
            this.defaultRenderCallback = null;
            this.imagePainter          = null;
            // WATERMeDIA PATCH - start

            // Here we patch callbacks adding a forced check for classloader
            // avoiding NPE on old versions of FORGE
            renderCallback = init$buildClassLoaderSafeCallback(renderCallback);
            // WATERMeDIA PATCH - end
        }

        this.mediaPlayer = this.mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.mediaPlayer.fullScreen().strategy(fullScreenStrategy);
        this.mediaPlayer.events().addMediaPlayerEventListener(this);
        this.mediaPlayer.events().addMediaEventListener(this);

        // WATERMeDIA PATCH - start

        // Here we patch callbacks adding a forced check for classloader
        // avoiding NPE on old versions of FORGE
        bufferFormatCallback = init$buildClassLoaderSafeCallback(bufferFormatCallback);
        // WATERMeDIA PATCH - end

        this.mediaPlayer.videoSurface().set(this.mediaPlayerFactory.videoSurfaces().newVideoSurface(bufferFormatCallback, renderCallback, lockBuffers, cleanupCallback));

        // WATERMeDIA Patch - REMOVED JPanel impl

        initInputEvents(inputEvents);

        onAfterConstruct();
    }

    // WATERMeDIA PATCH - start
    private BufferFormatCallback init$buildClassLoaderSafeCallback(BufferFormatCallback cb) {
        return new BufferFormatCallback() {
            @Override
            public void allocatedBuffers(ByteBuffer[] buffers) {
                VideoLan4J.checkClassLoader(mediaPlayer.getClassLoader());
                cb.allocatedBuffers(buffers);
            }

            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                VideoLan4J.checkClassLoader(mediaPlayer.getClassLoader());
                return cb.getBufferFormat(sourceWidth, sourceHeight);
            }
        };
    }

    private RenderCallback init$buildClassLoaderSafeCallback(RenderCallback cb) {
        return (mediaPlayer, nativeBuffers, bufferFormat) -> {
            VideoLan4J.checkClassLoader(mediaPlayer.getClassLoader());
            cb.display(mediaPlayer, nativeBuffers, bufferFormat);
        };
    }

    public CallbackMediaPlayerComponent(MediaPlayerFactory mediaPlayerFactory, boolean lockBuffers, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        this(mediaPlayerFactory, null, null, lockBuffers, null, renderCallback, bufferFormatCallback, cleanupCallback);
    }
    // WATERMeDIA PATCH - end

    /**
     * Construct a callback media list player component for intrinsic rendering (by this component).
     *
     * @param mediaPlayerFactory media player factory
     * @param fullScreenStrategy full screen strategy
     * @param inputEvents keyboard/mouse input event configuration
     * @param lockBuffers <code>true</code> if the native video buffer should be locked; <code>false</code> if not
     * @param imagePainter image painter (video renderer)
     */
    public CallbackMediaPlayerComponent(MediaPlayerFactory mediaPlayerFactory, FullScreenStrategy fullScreenStrategy, InputEvents inputEvents, boolean lockBuffers, CallbackImagePainter imagePainter) {
        this(mediaPlayerFactory, fullScreenStrategy, inputEvents, lockBuffers, imagePainter, null, null, null);
    }

    /**
     * Construct a callback media list player component for external rendering (by the client application).
     *
     * @param mediaPlayerFactory media player factory
     * @param fullScreenStrategy full screen strategy
     * @param inputEvents keyboard/mouse input event configuration
     * @param lockBuffers <code>true</code> if the native video buffer should be locked; <code>false</code> if not
     * @param renderCallback render callback
     * @param bufferFormatCallback buffer format callback
     * @param cleanupCallback cleanup callback
     */
    public CallbackMediaPlayerComponent(MediaPlayerFactory mediaPlayerFactory, FullScreenStrategy fullScreenStrategy, InputEvents inputEvents, boolean lockBuffers, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        this(mediaPlayerFactory, fullScreenStrategy, inputEvents, lockBuffers, null, renderCallback, bufferFormatCallback, cleanupCallback);
    }

    /**
     * Create a callback media player component from a builder.
     *
     * @param spec builder
     */
    public CallbackMediaPlayerComponent(MediaPlayerSpecs.CallbackMediaPlayerSpec spec) {
        this(spec.factory, spec.fullScreenStrategy, spec.inputEvents, spec.lockedBuffers, spec.imagePainter, spec.renderCallback, spec.bufferFormatCallback, spec.cleanupCallback);
    }

    /**
     * Create a callback media player component with LibVLC initialisation arguments and reasonable defaults.
     *
     * @param libvlcArgs LibVLC initialisation arguments
     */
    public CallbackMediaPlayerComponent(String... libvlcArgs) {
        this(new MediaPlayerFactory(libvlcArgs), null, null, true, null, null, null, null);
    }

    /**
     * Create a callback media player component with reasonable defaults.
     */
    public CallbackMediaPlayerComponent() {
        this(null, null, null, true, null, null, null, null);
    }

    /**
     * Validate that arguments are passed for either intrinsic or external rendering, but not both.
     *
     * @param imagePainter image painter (video renderer)
     * @param renderCallback render callback
     * @param bufferFormatCallback buffer format callback
     */
    private void validateArguments(CallbackImagePainter imagePainter, RenderCallback renderCallback, BufferFormatCallback bufferFormatCallback, BufferCleanupCallback cleanupCallback) {
        if (renderCallback == null) {
            if (bufferFormatCallback  != null) throw new IllegalArgumentException("Do not specify bufferFormatCallback without a renderCallback");
            if (cleanupCallback != null) throw new IllegalArgumentException("Do not specify cleanupCallback without a renderCallback");
        } else {
            if (imagePainter          != null) throw new IllegalArgumentException("Do not specify imagePainter with a renderCallback");
            if (bufferFormatCallback  == null) throw new IllegalArgumentException("bufferFormatCallback is required with a renderCallback");
        }
    }

    private MediaPlayerFactory initMediaPlayerFactory(MediaPlayerFactory mediaPlayerFactory) {
        if (mediaPlayerFactory == null) {
            mediaPlayerFactory = new MediaPlayerFactory(DEFAULT_FACTORY_ARGUMENTS);
        }
        return mediaPlayerFactory;
    }

    private void initInputEvents(InputEvents inputEvents) {
        if (inputEvents == null) {
            inputEvents = Platform.isLinux() || Platform.isMac() ? InputEvents.DEFAULT : InputEvents.DISABLE_NATIVE;
        }
        switch (inputEvents) {
            case NONE:
                break;
            case DISABLE_NATIVE:
                mediaPlayer.input().enableKeyInputHandling(false);
                mediaPlayer.input().enableMouseInputHandling(false);
                // Case fall-through is by design
            case DEFAULT:
                break;
        }
    }

    /**
     * Set a new image painter.
     * <p>
     * The image painter should only be changed when the media is stopped, changing an image painter during playback has
     * undefined behaviour.
     * <p>
     * This is <em>not</em> used if the application has supplied its own {@link RenderCallback} on instance creation.
     *
     * @param imagePainter image painter
     */
    public final void setImagePainter(CallbackImagePainter imagePainter) {
        this.imagePainter = imagePainter;
    }

    /**
     * Get the embedded media player reference.
     * <p>
     * An application uses this handle to control the media player, add listeners, and so on.
     *
     * @return media player
     */
    public final EmbeddedMediaPlayer mediaPlayer() {
        return mediaPlayer;
    }

    // WATERMeDIA Patch - removed JComponent getter
    /**
     * Release the media player component and the associated native media player resources.
     */
    public final void release() {
        onBeforeRelease();

        mediaPlayer.release();

        if (ownFactory) {
            mediaPlayerFactory.release();
        }

        onAfterRelease();
    }

    @Override
    public final MediaPlayerFactory mediaPlayerFactory() {
        return mediaPlayerFactory;
    }

    // WATERMeDIA Patch - Removed JPanel VideoSurface
    /**
     * Default implementation of a buffer format callback that returns a buffer format suitable for rendering into a
     * {@link BufferedImage}.
     */
    private class DefaultBufferFormatCallback extends BufferFormatCallbackAdapter {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            newVideoBuffer(sourceWidth, sourceHeight);
            return new RV32BufferFormat(sourceWidth, sourceHeight);
        }

    }

    /**
     * Used when the default buffer format callback is invoked to setup a new video buffer.
     * <p>
     * Here we create a new image to match the video size, and set the data buffer within that image as the data buffer
     * in the {@link DefaultRenderCallback}.
     * <p>
     * We also set a new preferred size on the video surface component in case the client application invalidates their
     * layout in anticipation of re-sizing their own window to accommodate the new video size.
     *
     * @param width width of the video
     * @param height height of the video
     */
    private void newVideoBuffer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        defaultRenderCallback.setImageBuffer(image);
    }

    /**
     * Default implementation of a render callback that copies video frame data directly to the data buffer of an image
     * raster.
     */
    private static class DefaultRenderCallback extends RenderCallbackAdapter {

        private void setImageBuffer(BufferedImage image) {
            setBuffer(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
        }

        @Override
        protected void onDisplay(MediaPlayer mediaPlayer, int[] buffer) {
        }
    }

    /**
     * Template methods to make it easy for a client application sub-class to render a lightweight overlay on top of the
     * video.
     * <p>
     * When this method is invoked the graphics context will already have a proper scaling applied according to the
     * video size.
     *
     * @param g2 graphics drawing context
     */
    protected void onPaintOverlay(Graphics2D g2) {
    }

}