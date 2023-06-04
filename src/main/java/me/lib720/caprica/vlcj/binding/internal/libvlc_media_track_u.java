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
 * Copyright 2009-2021 Caprica Software Limited.
 */

package me.lib720.caprica.vlcj.binding.internal;

import com.sun.jna.Union;

/**
 *
 */
public class libvlc_media_track_u extends Union {

    public static class ByReference extends libvlc_media_track_u implements Union.ByReference {}

    public libvlc_audio_track_t audio;
    public libvlc_video_track_t video;
    public libvlc_subtitle_track_t subtitle;

}