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

package org.watermedia.videolan4j.media;

import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_media_list_t;
import org.watermedia.videolan4j.medialist.MediaList;
import org.watermedia.videolan4j.medialist.MediaListRef;

public final class SubitemApi extends BaseApi {

    SubitemApi(Media media) {
        super(media);
    }

    /**
     * Get the subitems as a {@link MediaList}.
     * <p>
     * The caller <em>must</em> release the returned {@link MediaList} when it no longer has a use for it
     *
     * @return subitems list
     */
    public MediaList newMediaList() {
        libvlc_media_list_t list = LibVlc.libvlc_media_subitems(mediaInstance);
        if (list != null) {
            return new MediaList(libvlcInstance, list);
        } else {
            return null;
        }
    }

    /**
     * Get the subitems as a {@link MediaListRef}.
     * <p>
     * The caller <em>must</em> release the returned {@link MediaListRef} when it no longer has a use for it
     *
     * @return subitems list reference
     */
    public MediaListRef newMediaListRef() {
        libvlc_media_list_t list = LibVlc.libvlc_media_subitems(mediaInstance);
        if (list != null) {
            return new MediaListRef(libvlcInstance, list);
        } else {
            return null;
        }
    }

}
