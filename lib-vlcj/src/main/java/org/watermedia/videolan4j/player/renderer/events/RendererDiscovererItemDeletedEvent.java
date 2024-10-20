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

package org.watermedia.videolan4j.player.renderer.events;

import org.watermedia.videolan4j.binding.internal.libvlc_event_t;
import org.watermedia.videolan4j.binding.internal.renderer_discoverer_item_deleted;
import org.watermedia.videolan4j.player.renderer.RendererDiscoverer;
import org.watermedia.videolan4j.player.renderer.RendererDiscovererEventListener;
import org.watermedia.videolan4j.player.renderer.RendererItem;

/**
 * Native event used when an item was deleted from the renderer discoverer.
 */
final class RendererDiscovererItemDeletedEvent extends RendererDiscovererEvent {

    private final RendererItem item;

    RendererDiscovererItemDeletedEvent(RendererDiscoverer rendererDiscoverer, libvlc_event_t event) {
        super(rendererDiscoverer);
        this.item = new RendererItem(((renderer_discoverer_item_deleted) event.u.getTypedValue(renderer_discoverer_item_deleted.class)).item);
    }

    @Override
    public void notify(RendererDiscovererEventListener listener) {
        listener.rendererDiscovererItemDeleted(rendererDiscoverer, item);
    }

}
