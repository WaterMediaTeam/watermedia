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

package me.lib720.caprica.vlcj4.media.events;

import me.lib720.caprica.vlcj4.binding.internal.libvlc_event_t;
import me.lib720.caprica.vlcj4.binding.internal.libvlc_instance_t;
import me.lib720.caprica.vlcj4.binding.internal.media_duration_changed;
import me.lib720.caprica.vlcj4.media.Media;
import me.lib720.caprica.vlcj4.media.MediaEventListener;

/**
 * Encapsulation of a media duration changed event.
 */
final class MediaDurationChangedEvent extends MediaEvent {

    private final long newDuration;

    /**
     * Create a media event.
     *
     * @param libvlcInstance native library instance
     * @param media component the event relates to
     * @param event native event
     */
    MediaDurationChangedEvent(libvlc_instance_t libvlcInstance, Media media, libvlc_event_t event) {
        super(libvlcInstance, media);
        this.newDuration = ((media_duration_changed) event.u.getTypedValue(media_duration_changed.class)).new_duration;
    }

    @Override
    public void notify(MediaEventListener listener) {
        listener.mediaDurationChanged(component, newDuration);
    }

}
