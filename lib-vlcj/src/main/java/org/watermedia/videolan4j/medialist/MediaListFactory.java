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

package org.watermedia.videolan4j.medialist;

import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_list_t;

/**
 * Factory to create {@link MediaList} and {@link MediaListRef} components.
 * <p>
 * <em>This factory is <strong>not</strong> intended for use by client applications.</em>
 */
public final class MediaListFactory {

    /**
     * Create a new {@link MediaListRef}.
     * <p>
     * The client application <em>must</em> release the returned {@link MediaListRef} when it no long has any use for
     * it.
     *
     * @param libvlcInstance native library instance
     * @return media list, or <code>null</code> on error
     */
    public static MediaListRef newMediaListRef(libvlc_instance_t libvlcInstance) {
        return createMediaListRef(libvlcInstance, LibVlc.libvlc_media_list_new(libvlcInstance));
    }

    /**
     * Create a new {@link MediaList}.
     * <p>
     * The client application <em>must</em> release the returned {@link MediaList} when it no longer has any use for it.
     *
     * @param libvlcInstance native library instance
     * @return media list reference, or <code>null</code> on error
     */
    public static MediaList newMediaList(libvlc_instance_t libvlcInstance) {
        return createMediaList(libvlcInstance, LibVlc.libvlc_media_list_new(libvlcInstance));
    }

    private static MediaListRef createMediaListRef(libvlc_instance_t libvlcInstance, libvlc_media_list_t mediaListInstance) {
        if (mediaListInstance != null) {
            return new MediaListRef(libvlcInstance, mediaListInstance);
        } else {
            return null;
        }
    }

    private static MediaList createMediaList(libvlc_instance_t libvlcInstance, libvlc_media_list_t mediaListInstance) {
        if (mediaListInstance != null) {
            return new MediaList(libvlcInstance, mediaListInstance);
        } else {
            return null;
        }
    }

    private MediaListFactory() {
    }

}
