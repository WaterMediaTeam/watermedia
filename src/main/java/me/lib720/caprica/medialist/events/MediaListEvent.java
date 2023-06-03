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
 * Copyright 2009-2022 Caprica Software Limited.
 */

package me.lib720.caprica.medialist.events;

import me.lib720.caprica.binding.internal.libvlc_instance_t;
import me.lib720.caprica.medialist.MediaList;
import me.lib720.caprica.medialist.MediaListEventListener;
import me.lib720.caprica.support.eventmanager.BaseEvent;

/**
 * Base implementation for media list events.
 * <p>
 * Every instance of an event refers to an associated media list.
 */
abstract class MediaListEvent extends BaseEvent<MediaList, MediaListEventListener> {

    /**
     * Create a media list event.
     *
     * @param libvlcInstance native library instance
     * @param mediaList media list that the event relates to
     */
    protected MediaListEvent(libvlc_instance_t libvlcInstance, MediaList mediaList) {
        super(libvlcInstance, mediaList);
    }

}
