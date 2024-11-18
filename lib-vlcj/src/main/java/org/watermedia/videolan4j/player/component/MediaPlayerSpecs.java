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

import org.watermedia.videolan4j.player.component.callback.CallbackImagePainter;
import org.watermedia.videolan4j.player.embedded.fullscreen.unsupported.UnsupportedFullScreenStrategy;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferCleanupCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormatCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.RenderCallback;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.player.embedded.fullscreen.FullScreenStrategy;
import org.watermedia.videolan4j.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import java.awt.*;

/**
 * Builders for the various media player components.
 * <p>
 * The builders can be used directly as constructor arguments for the corresponding component, or can be used to create
 * a component. The former case is useful when you are dynamically sub-classing the component.
 * <p>
 * Note that components and their "list" component counterparts use the same builders.
 * <p>
 * Generally, all component constructor (and therefore builder) parameters are optional, components will themselves
 * provide reasonable defaults.
 * @watermedia removed embedded spec
 */
public final class MediaPlayerSpecs {

    /**
     * Create a new builder for a callback media player, or a callback media list player.
     *
     * @return callback media player builder
     */
    public static CallbackMediaPlayerSpec callbackMediaPlayerSpec() {
        return new CallbackMediaPlayerSpec();
    }

    /**
     * Create a new builder for an audio player, or an audio list player.
     *
     * @return embedded media player builder
     */
    public static AudioPlayerSpec audioPlayerSpec() {
        return new AudioPlayerSpec();
    }

    /**
     * Builder for a callback media or callback media list player.
     */
    public static final class CallbackMediaPlayerSpec {

        MediaPlayerFactory factory;
        FullScreenStrategy fullScreenStrategy;
        InputEvents inputEvents;
        boolean lockedBuffers = true;
        CallbackImagePainter imagePainter;
        RenderCallback renderCallback;
        BufferFormatCallback bufferFormatCallback;
        BufferCleanupCallback cleanupCallback;

        /**
         * Specify the media player factory to use.
         *
         * @param factory media player factory
         * @return this builder
         */
        public CallbackMediaPlayerSpec withFactory(MediaPlayerFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * Specify the full-screen strategy to use.
         * <p>
         * By default if no strategy is set there will be no support for full-screen mode.
         *
         * @param fullScreenStrategy full-screen strategy
         * @return this builder
         */
        public CallbackMediaPlayerSpec withFullScreenStrategy(FullScreenStrategy fullScreenStrategy) {
            this.fullScreenStrategy = fullScreenStrategy;
            return this;
        }

        /**
         * Specify to use the default full-screen strategy.
         * <p>
         * The default strategy will use the "best" available native strategy depending on the run-time operating
         * system.
         *
         * @param fullScreenWindow window that will be made full-screen (the window containing the video surface)
         * @return this builder
         */
        public CallbackMediaPlayerSpec withDefaultFullScreenStrategy(Window fullScreenWindow) {
            this.fullScreenStrategy = new AdaptiveFullScreenStrategy(fullScreenWindow);
            return this;
        }

        /**
         * Specify to use the do-nothing unsupported full-screen strategy.
         * <p>
         * This is not really necessary as the default situation is to have no full-screen strategy.
         *
         * @return this builder
         */
        public CallbackMediaPlayerSpec withUnsupportedFullScreenStrategy() {
            this.fullScreenStrategy = new UnsupportedFullScreenStrategy();
            return this;
        }

        /**
         * Specify keyboard/mouse input-event configuration.
         *
         * @param inputEvents  keyboard/mouse configuration
         * @return this builder
         */
        public CallbackMediaPlayerSpec withInputEvents(InputEvents inputEvents) {
            this.inputEvents = inputEvents;
            return this;
        }

        /**
         * Specify whether or not the native video frame buffer should use operating system primitives to "lock" the
         * native memory (the aim is to prevent the native memory from being swapped to disk).
         * <p>
         * Buffers <em>are</em> locked by default.
         *
         * @param lockedBuffers <code>true</code> if the buffers should be locked; <code>false</code> if they should not
         * @return this builder
         */
        public CallbackMediaPlayerSpec withLockedBuffers(boolean lockedBuffers) {
            this.lockedBuffers = lockedBuffers;
            return this;
        }

        /**
         * Specify whether or not the native video frame buffer should use operating system primitives to "lock" the
         * native memory (the aim is to prevent the native memory from being swapped to disk).
         * <p>
         * Buffers <em>are</em> locked by default.
         * <p>
         * This method is unnecessary at the moment but is supplied in case the default changes to <code>false</code> in
         * the future.
         *
         * @return this builder
         */
        public CallbackMediaPlayerSpec withLockedBuffers() {
            this.lockedBuffers = true;
            return this;
        }

        /**
         * Specify the image painter (video renderer) to use.
         *
         * @param imagePainter image painter
         * @return this builder
         */
        public CallbackMediaPlayerSpec withImagePainter(CallbackImagePainter imagePainter) {
            this.imagePainter = imagePainter;
            return this;
        }

        /**
         * Specify the render callback to use.
         * <p>
         * A render callback is used where the application intends to take care of rendering the video itself.
         *
         * @param renderCallback render callback
         * @return this builder
         */
        public CallbackMediaPlayerSpec withRenderCallback(RenderCallback renderCallback) {
            this.renderCallback = renderCallback;
            return this;
        }

        /**
         * Specify the buffer format callback to use.
         * <p>
         * A buffer format callback is used where the application intends to take care of rendering the video itself.
         *
         * @param bufferFormatCallback buffer format callback
         * @return this builder
         */
        public CallbackMediaPlayerSpec withBufferFormatCallback(BufferFormatCallback bufferFormatCallback) {
            this.bufferFormatCallback = bufferFormatCallback;
            return this;
        }

        /**
         * Create a callback media player component from this builder.
         *
         * @return callback media player component
         */
        public CallbackMediaPlayerComponent callbackMediaPlayer() {
            return new CallbackMediaPlayerComponent(this);
        }

        /**
         * Create a callback media list player component from this builder.
         *
         * @return callback media list player component
         */
        public CallbackMediaListPlayerComponent callbackMediaListPlayer() {
            return new CallbackMediaListPlayerComponent(this);
        }

        private CallbackMediaPlayerSpec() {
        }
        
    }

    /**
     * Builder for an audio or audio list player.
     */
    public static final class AudioPlayerSpec {

        MediaPlayerFactory factory;

        /**
         * Specify the media player factory to use.
         *
         * @param factory media player factory
         * @return this builder
         */
        public AudioPlayerSpec withFactory(MediaPlayerFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * Create an audio player component from this builder.
         *
         * @return audio player component
         */
        public AudioPlayerComponent audioPlayer() {
            return new AudioPlayerComponent(this);
        }

        /**
         * Create an audio list player component from this builder.
         *
         * @return audio list player component
         */
        public AudioListPlayerComponent audioListPlayer() {
            return new AudioListPlayerComponent(this);
        }

        private AudioPlayerSpec() {
        }

    }

}
