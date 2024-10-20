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

package org.watermedia.videolan4j.player.list.events;

import org.watermedia.videolan4j.player.list.MediaListPlayer;
import org.watermedia.videolan4j.player.list.MediaListPlayerEventListener;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;

/**
 * Encapsulation of a media list player played event.
 * <p>
 * The name of this native event is confusing, it actually means that the entire list <em>finished</em> playing.
 */
final class MediaListPlayerPlayedEvent extends MediaListPlayerEvent {

    /**
     * Create a media player event.
     *
     * @param libvlcInstance native library instance
     * @param mediaListPlayer media player the event relates to
     */
    MediaListPlayerPlayedEvent(libvlc_instance_t libvlcInstance, MediaListPlayer mediaListPlayer) {
        super(libvlcInstance, mediaListPlayer);
    }

    @Override
    public void notify(MediaListPlayerEventListener listener) {
        listener.mediaListPlayerFinished(component);
    }

}
