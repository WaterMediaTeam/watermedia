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

import org.watermedia.videolan4j.binding.internal.libvlc_event_t;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_t;
import org.watermedia.videolan4j.binding.internal.media_list_will_add_item;
import org.watermedia.videolan4j.medialist.MediaList;
import org.watermedia.videolan4j.medialist.MediaListEventListener;

/**
 * Encapsulation of a media list will add item event.
 */
final class MediaListWillAddItemEvent extends MediaListEvent {

    /**
     * Native media instance that will be added.
     */
    private final libvlc_media_t item;

    /**
     * Index at which the item will be added.
     */
    private final int index;

    /**
     * Create a media list event.
     *
     * @param libvlcInstance native library instance
     * @param mediaList media list the event relates to
     * @param event native event
     */
    MediaListWillAddItemEvent(libvlc_instance_t libvlcInstance, MediaList mediaList, libvlc_event_t event) {
        super(libvlcInstance, mediaList);

        media_list_will_add_item addItemEvent = ((media_list_will_add_item) event.u.getTypedValue(media_list_will_add_item.class));

        this.item  = addItemEvent.item;
        this.index = addItemEvent.index;
    }

    @Override
    public void notify(MediaListEventListener listener) {
        listener.mediaListWillAddItem(component, temporaryMediaRef(item), index);
    }

}
