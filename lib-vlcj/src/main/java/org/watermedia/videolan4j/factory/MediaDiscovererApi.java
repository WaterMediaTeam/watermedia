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

package org.watermedia.videolan4j.factory;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import org.watermedia.videolan4j.binding.internal.libvlc_media_discoverer_description_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_discoverer_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.lib.size_t;
import org.watermedia.videolan4j.media.discoverer.MediaDiscoverer;
import org.watermedia.videolan4j.media.discoverer.MediaDiscovererCategory;
import org.watermedia.videolan4j.media.discoverer.MediaDiscovererDescription;
import org.watermedia.videolan4j.media.discoverer.MediaDiscovererFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Behaviour pertaining to media discovery.
 */
public final class MediaDiscovererApi extends BaseApi {

    MediaDiscovererApi(MediaPlayerFactory factory) {
        super(factory);
    }

    /**
     * Get the list of media discoverer descriptions for a particular category.
     *
     * @param category desired category
     * @return media discoverer descriptions, will not be <code>null</code>
     */
    public List<MediaDiscovererDescription> discoverers(MediaDiscovererCategory category) {
        PointerByReference ref = new PointerByReference();
        size_t size = LibVlc.libvlc_media_discoverer_list_get(libvlcInstance, category.intValue(), ref);
        try {
            int count = size.intValue();
            List<MediaDiscovererDescription> result = new ArrayList<MediaDiscovererDescription>(count);
            if (count > 0) {
                Pointer[] pointers = ref.getValue().getPointerArray(0, count);
                for (Pointer pointer : pointers) {
                    // WATERMeDIA PATCH - start
                     libvlc_media_discoverer_description_t description = (libvlc_media_discoverer_description_t) Structure.newInstance(libvlc_media_discoverer_description_t.class, pointer);
//                    libvlc_media_discoverer_description_t description = ReflectTool.invokeWithReturn("newInstance", Structure.class, null, libvlc_media_discoverer_description_t.class, pointer);
                    // WATERMeDIA PATCH - end
                    description.read();
                    result.add(new MediaDiscovererDescription(description.psz_name, description.psz_longname, MediaDiscovererCategory.mediaDiscovererCategory(description.i_cat)));
                }
            }
            return result;
        }
        finally {
            LibVlc.libvlc_media_discoverer_list_release(ref.getValue(), size);
        }
    }

    /**
     * Get a named media discoverer.
     * <p>
     * Use {@link #discoverers(MediaDiscovererCategory)} to get the name.
     *
     * @param name media discoverer name
     * @return media discoverer, may be <code>null</code>
     */
    public MediaDiscoverer discoverer(String name) {
        libvlc_media_discoverer_t discoverer = LibVlc.libvlc_media_discoverer_new(libvlcInstance, name);
        if (discoverer != null) {
            return MediaDiscovererFactory.newMediaDiscoverer(libvlcInstance, discoverer);
        } else {
            return null;
        }
    }

}