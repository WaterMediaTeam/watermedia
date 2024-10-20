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

import org.watermedia.videolan4j.binding.internal.libvlc_renderer_discoverer_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;

/**
 * Encapsulation of a native renderer discoverer instance.
 */
public final class RendererDiscoverer {

    /**
     * Native renderer discoverer instance.
     */
    final libvlc_renderer_discoverer_t discovererInstance;

    private final EventApi eventApi;
    private final ListApi  listApi;

    public RendererDiscoverer(libvlc_renderer_discoverer_t discoverer) {
        this.discovererInstance = discoverer;

        this.eventApi = new EventApi(this);
        this.listApi  = new ListApi(this);
    }

    /**
     * Behaviour pertaining to events.
     *
     * @return event behaviour
     */
    public EventApi events() {
        return eventApi;
    }

    /**
     * Behaviour pertaining to the list of discovered renderer items.
     *
     * @return renderer discoverer item list behaviour
     */
    public ListApi list() {
        return listApi;
    }

    /**
     * Start discovery.
     *
     * @return <code>true</code> if successful; <code>false</code> on error
     */
    public boolean start() {
        return LibVlc.libvlc_renderer_discoverer_start(discovererInstance) == 0;
    }

    /**
     * Stop discovery.
     */
    public void stop() {
        LibVlc.libvlc_renderer_discoverer_stop(discovererInstance);
    }

    /**
     * Release this component and the associated native resources.
     * <p>
     * The component must no longer be used.
     */
    public void release() {
        eventApi.release();
        listApi .release();

        LibVlc.libvlc_renderer_discoverer_release(discovererInstance);
    }

    /**
     * Get the associated native renderer discoverer instance.
     *
     * @return renderer discoverer instance
     */
    public libvlc_renderer_discoverer_t rendererDiscovererInstance() {
        return discovererInstance;
    }

}
