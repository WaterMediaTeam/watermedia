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

package org.watermedia.videolan4j.medialist.events;

import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.medialist.MediaList;
import org.watermedia.videolan4j.medialist.MediaListEventListener;

/**
 * Encapsulation of a media list end reached event.
 */
final class MediaListEndReachedEvent extends MediaListEvent {

    /**
     * Create a media list event.
     *
     * @param libvlcInstance native library instance
     * @param mediaList media list the event relates to
     */
    MediaListEndReachedEvent(libvlc_instance_t libvlcInstance, MediaList mediaList) {
        super(libvlcInstance, mediaList);
    }

    @Override
    public void notify(MediaListEventListener listener) {
        listener.mediaListEndReached(component);
    }

}
