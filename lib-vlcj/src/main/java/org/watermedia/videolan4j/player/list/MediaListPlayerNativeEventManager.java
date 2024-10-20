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

package org.watermedia.videolan4j.player.list;

import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_event_e;
import org.watermedia.videolan4j.binding.internal.libvlc_event_manager_t;
import org.watermedia.videolan4j.binding.internal.libvlc_event_t;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.player.list.events.MediaListPlayerEventFactory;
import org.watermedia.videolan4j.support.eventmanager.EventNotification;
import org.watermedia.videolan4j.support.eventmanager.NativeEventManager;

final public class MediaListPlayerNativeEventManager extends NativeEventManager<MediaListPlayer, MediaListPlayerEventListener> {

    MediaListPlayerNativeEventManager(libvlc_instance_t libvlcInstance, MediaListPlayer eventObject) {
        super(libvlcInstance, eventObject, libvlc_event_e.libvlc_MediaListPlayerPlayed, libvlc_event_e.libvlc_MediaListPlayerStopped, "media-list-player-events");
    }

    @Override
    protected libvlc_event_manager_t onGetEventManager(MediaListPlayer eventObject) {
        return LibVlc.libvlc_media_list_player_event_manager(eventObject.mediaListPlayerInstance());
    }

    @Override
    protected EventNotification<MediaListPlayerEventListener> onCreateEvent(libvlc_instance_t libvlcInstance, libvlc_event_t event, MediaListPlayer eventObject) {
        return MediaListPlayerEventFactory.createEvent(libvlcInstance, eventObject, event);
    }

}
