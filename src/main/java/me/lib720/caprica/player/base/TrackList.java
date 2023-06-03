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
 * Copyright 2009-2022 Caprica Software Limited.
 */

package me.lib720.caprica.player.base;

import me.lib720.caprica.binding.lib.LibVlc;
import me.lib720.caprica.binding.internal.libvlc_media_track_t;
import me.lib720.caprica.binding.internal.libvlc_media_tracklist_t;
import me.lib720.caprica.binding.support.types.size_t;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

abstract public class TrackList<T extends Track> {

    private final libvlc_media_tracklist_t instance;

    private final List<T> tracks;

    TrackList(libvlc_media_tracklist_t instance) {
        this.instance = instance;
        this.tracks = initTracks();
    }

    /**
     * Get the list of tracks.
     * <p>
     * The track instances returned by this method must <strong>not</strong> be released by {@link Track#release()}.
     *
     * @return list of tracks
     */
    public final List<T> tracks() {
        return tracks;
    }

    /**
     * Release the native resources associated with this track list and the associated tracks.
     */
    public final void release() {
        LibVlc.libvlc_media_tracklist_delete(instance);
    }

    private List<T> initTracks() {
        int trackCount = LibVlc.libvlc_media_tracklist_count(instance).intValue();
        List<T> tracks = new ArrayList<T>(trackCount);
        for (int i = 0; i < trackCount; i++) {
            // This instance must NOT be released via libvlc_media_track_release
            libvlc_media_track_t track = LibVlc.libvlc_media_tracklist_at(instance, new size_t(i));
            tracks.add(initTrack(track));
        }
        return unmodifiableList(tracks);
    }

    protected abstract T initTrack(libvlc_media_track_t trackInstance);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString()).append('[');
        sb.append("tracks=").append(tracks).append(']');
        return sb.toString();
    }
}
