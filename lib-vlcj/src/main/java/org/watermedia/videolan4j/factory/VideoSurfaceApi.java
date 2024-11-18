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

import org.watermedia.videolan4j.player.embedded.videosurface.CallbackVideoSurface;
import org.watermedia.videolan4j.player.embedded.videosurface.VideoSurfaceAdapters;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferCleanupCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.BufferFormatCallback;
import org.watermedia.videolan4j.player.embedded.videosurface.callback.RenderCallback;

/**
 * Behaviour pertaining to the creation of video surfaces.
 * @watermedia patched to remove VLCJ component implementation
 */
public final class VideoSurfaceApi extends BaseApi {

    VideoSurfaceApi(MediaPlayerFactory factory) {
        super(factory);
    }

    /**
     * Create a new video surface for "direct" rendering via callbacks.
     *
     * @param bufferFormatCallback buffer format callback
     * @param renderCallback render callback
     * @param lockBuffers <code>true</code> if the video buffer should be locked; <code>false</code> if it should not
     * @return video surface
     */
    public CallbackVideoSurface newVideoSurface(BufferFormatCallback bufferFormatCallback, RenderCallback renderCallback, boolean lockBuffers, BufferCleanupCallback cleanupCallback) {
        return new CallbackVideoSurface(bufferFormatCallback, renderCallback, lockBuffers, VideoSurfaceAdapters.getVideoSurfaceAdapter(), cleanupCallback);
    }

}
