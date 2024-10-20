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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.binding.internal.libvlc_media_slave_t;
import org.watermedia.videolan4j.binding.internal.libvlc_media_t;
import org.watermedia.videolan4j.binding.lib.LibVlc;

import java.util.ArrayList;
import java.util.List;

final class MediaSlaves {

    private MediaSlaves() {
    }

    static List<MediaSlave> getMediaSlaves(libvlc_media_t media) {
        PointerByReference slavesPointer = new PointerByReference();
        int numberOfSlaves = LibVlc.libvlc_media_slaves_get(media, slavesPointer);
        List<MediaSlave> result = new ArrayList<MediaSlave>(numberOfSlaves);
        if (numberOfSlaves > 0) {
            Pointer[] pointers = slavesPointer.getValue().getPointerArray(0, numberOfSlaves);
            for (Pointer pointer : pointers) {
                libvlc_media_slave_t slave = new libvlc_media_slave_t(pointer);
                result.add(new MediaSlave(VideoLan4J.copyNativeString(slave.psz_uri), MediaSlaveType.mediaSlaveType(slave.i_type), slave.i_priority));
            }
        }
        // In this case the native structure must be freed even if the count is zero
        LibVlc.libvlc_media_slaves_release(slavesPointer.getValue(), numberOfSlaves);
        return result;
    }

}
