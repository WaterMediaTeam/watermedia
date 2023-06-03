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

package me.lib720.caprica.media;

import me.lib720.caprica.binding.lib.LibVlc;
import me.lib720.caprica.binding.support.strings.NativeString;
import me.lib720.caprica.binding.internal.libvlc_media_stats_t;

/**
 * Behaviour pertaining to media information, providing things like the media resource locator, type, state and duration
 * of this media. Also provides access to the various media tracks and playback statistics.
 */
public final class InfoApi extends BaseApi {

    private final libvlc_media_stats_t statsInstance = new libvlc_media_stats_t();

    InfoApi(Media media) {
        super(media);
    }

    /**
     * Get the media resource locator for the current media.
     *
     * @return media resource locator
     */
    public String mrl() {
        return NativeString.copyAndFreeNativeString(LibVlc.libvlc_media_get_mrl(mediaInstance));
    }

    /**
     * Get the current media type
     *
     * @return media type
     */
    public MediaType type() {
        return MediaType.mediaType(LibVlc.libvlc_media_get_type(mediaInstance));
    }

    /**
     * Get the duration
     *
     * @return duration, milliseconds
     */
    public long duration() {
        return LibVlc.libvlc_media_get_duration(mediaInstance);
    }

    /**
     * Get playback statistics for the current media.
     *
     * @param mediaStatistics caller-supplied structure to receive the media statistics
     * @return <code>true</code> if the statistics were updated; <code>false</code> on error
     */
    public boolean statistics(MediaStatistics mediaStatistics) {
        if (LibVlc.libvlc_media_get_stats(mediaInstance, statsInstance) != 0) {
            mediaStatistics.apply(statsInstance);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get playback statistics for the current media.
     *
     * @return media statistics, or <code>null</code> on error
     */
    public MediaStatistics statistics() {
        if (LibVlc.libvlc_media_get_stats(mediaInstance, statsInstance) != 0) {
            MediaStatistics mediaStatistics = new MediaStatistics();
            mediaStatistics.apply(statsInstance);
            return mediaStatistics;
        } else {
            return null;
        }
    }

}
