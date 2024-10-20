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

package org.watermedia.videolan4j.player.renderer;

import org.watermedia.videolan4j.player.renderer.events.RendererDiscovererEventFactory;
import org.watermedia.videolan4j.support.eventmanager.EventNotification;
import org.watermedia.videolan4j.support.eventmanager.NativeEventManager;
import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_event_e;
import org.watermedia.videolan4j.binding.internal.libvlc_event_manager_t;
import org.watermedia.videolan4j.binding.internal.libvlc_event_t;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;

/**
 * Native event manager implementation for the {@link RendererDiscoverer}.
 * <p>
 * For {@link #onCreateEvent(libvlc_instance_t, libvlc_event_t, RendererDiscoverer)} in this component, the
 * <code>libvlcInstance</code> parameter will be <code>null</code>.
 */
final class RendererDiscovererNativeEventManager extends NativeEventManager<RendererDiscoverer, RendererDiscovererEventListener> {

    RendererDiscovererNativeEventManager(RendererDiscoverer eventObject) {
        super(null, eventObject, libvlc_event_e.libvlc_RendererDiscovererItemAdded, libvlc_event_e.libvlc_RendererDiscovererItemDeleted, "renderer-discoverer-events");
    }

    @Override
    protected libvlc_event_manager_t onGetEventManager(RendererDiscoverer eventObject) {
        return LibVlc.libvlc_renderer_discoverer_event_manager(eventObject.rendererDiscovererInstance());
    }

    @Override
    protected EventNotification<RendererDiscovererEventListener> onCreateEvent(libvlc_instance_t libvlcInstance, libvlc_event_t event, RendererDiscoverer eventObject) {
        return RendererDiscovererEventFactory.createEvent(eventObject, event);
    }

}
