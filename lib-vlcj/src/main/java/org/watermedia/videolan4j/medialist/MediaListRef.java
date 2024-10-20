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
 * An opaque reference to a media list.
 * <p>
 * This is used to pass around media list references without requiring the full-blown {@link MediaList} component.
 */
public final class MediaListRef {

    /**
     * Native library instance.
     */
    private final libvlc_instance_t libvlcInstance;

    /**
     * Native media list instance.
     */
    private final libvlc_media_list_t mediaListInstance;

    /**
     * Create a media list reference.
     *
     * @param libvlcInstance native library instance
     * @param mediaListInstance native media lis tinstance
     */
    public MediaListRef(libvlc_instance_t libvlcInstance, libvlc_media_list_t mediaListInstance) {
        this.libvlcInstance = libvlcInstance;
        this.mediaListInstance = mediaListInstance;
    }

    /**
     * Get the native media list instance.
     *
     * @return native media list instance
     */
    public libvlc_media_list_t mediaListInstance() {
        return mediaListInstance;
    }

    /**
     * Create a new {@link MediaList} for this {@link MediaListRef}.
     * <p>
     * The caller <em>must</em> release the returned {@link MediaList} when it has no further use for it.
     *
     * @return media list
     */
    public MediaList newMediaList() {
        LibVlc.libvlc_media_list_retain(mediaListInstance);
        return new MediaList(libvlcInstance, mediaListInstance);
    }

    /**
     * Create a new {@link MediaListRef} for this {@link MediaListRef}.
     * <p>
     * The caller <em>must</em> release the returned {@link MediaListRef} when it has no further use for it.
     *
     * @return media list reference
     */
    public MediaListRef newMediaListRef() {
        LibVlc.libvlc_media_list_retain(mediaListInstance);
        return new MediaListRef(libvlcInstance, mediaListInstance);
    }

    /**
     * Release associated native media list instance.
     * <p>
     * This component must not longer be used.
     */
    public void release() {
        LibVlc.libvlc_media_list_release(mediaListInstance);
    }

}
