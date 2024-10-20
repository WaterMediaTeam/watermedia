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

package org.watermedia.videolan4j.player.embedded;

import org.watermedia.videolan4j.binding.lib.LibVlc;

/**
 * Behaviour pertaining to keyboard/mouse input-events.
 */
public final class InputApi extends BaseApi {

    InputApi(EmbeddedMediaPlayer mediaPlayer) {
        super(mediaPlayer);
    }

    /**
     * Set whether or not to enable native media player mouse input handling.
     * <p>
     * It may be necessary on some platforms to invoke this method with a <code>false</code> parameter value for Java
     * mouse and keyboard listeners to work.
     * <p>
     * Note that clicking the video surface on certain platforms is not guaranteed to capture mouse events - it may be
     * necessary to respond to a mouse pressed event on the video surface and explicitly request the input focus to the
     * video surface.
     *
     * @param enable <code>true</code> to enable, <code>false</code> to disable
     */
    public void enableMouseInputHandling(boolean enable) {
        LibVlc.libvlc_video_set_mouse_input(mediaPlayerInstance, enable ? 1 : 0);
    }

    /**
     * Set whether or not to enable native media player keyboard input handling.
     * <p>
     * It may be necessary on some platforms to invoke this method with a <code>false</code> parameter value for Java
     * mouse and keyboard listeners to work.
     *
     * @param enable <code>true</code> to enable, <code>false</code> to disable
     */
    public void enableKeyInputHandling(boolean enable) {
        LibVlc.libvlc_video_set_key_input(mediaPlayerInstance, enable ? 1 : 0);
    }

}
