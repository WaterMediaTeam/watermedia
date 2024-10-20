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
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import org.watermedia.videolan4j.VideoLan4J;
import org.watermedia.videolan4j.binding.internal.*;
import org.watermedia.videolan4j.binding.lib.LibVlc;

import java.util.*;

final class TrackInformation {

    static List<TrackInfo> getTrackInfo(libvlc_media_t media, TrackType... types) {
        List<TrackInfo> result;
        if (media != null) {
            Set<TrackType> requestedTypes;
            if (types == null || types.length == 0) {
                requestedTypes = null;
            }
            else {
                requestedTypes = new HashSet<TrackType>(types.length);
                for (TrackType type : types) {
                    requestedTypes.add(type);
                }
            }
            result = TrackInformation.getTrackInfo(media, requestedTypes);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    private static List<TrackInfo> getTrackInfo(libvlc_media_t media, Set<TrackType> types) {
        PointerByReference tracksPointer = new PointerByReference();
        int numberOfTracks = LibVlc.libvlc_media_tracks_get(media, tracksPointer);
        List<TrackInfo> result = new ArrayList<TrackInfo>(numberOfTracks);
        if (numberOfTracks > 0) {
            Pointer[] tracks = tracksPointer.getValue().getPointerArray(0, numberOfTracks);
            for (Pointer track : tracks) {
                TrackInfo trackInfo = getTrackInfo(track, types);
                if (trackInfo != null) {
                    result.add(trackInfo);
                }
            }
            LibVlc.libvlc_media_tracks_release(tracksPointer.getValue(), numberOfTracks);
        }
        return result;
    }

    private static TrackInfo getTrackInfo(Pointer pointer, Set<TrackType> types) {
        TrackInfo result = null;
        // WATERMeDIA PATCH - start
         libvlc_media_track_t track = (libvlc_media_track_t) Structure.newInstance(libvlc_media_track_t.class, pointer);
//        libvlc_media_track_t track = ReflectTool.invokeWithReturn("newInstance", Structure.class, null, libvlc_media_track_t.class, pointer);
        // WATERMeDIA PATCH - end

        track.read();
        switch (TrackType.trackType(track.i_type)) {
            case UNKNOWN:
                if (types == null || types.contains(TrackType.UNKNOWN)) {
                    result = getUnknownTrackInfo(track);
                }
                break;

            case VIDEO:
                if (types == null || types.contains(TrackType.VIDEO)) {
                    result = getVideoTrackInfo(track);
                }
                break;

            case AUDIO:
                if (types == null || types.contains(TrackType.AUDIO)) {
                    result = getAudioTrackInfo(track);
                }
                break;

            case TEXT:
                if (types == null || types.contains(TrackType.TEXT)) {
                    result = getTextTrackInfo(track);
                }
                break;
        }
        return result;
    }

    private static TrackInfo getUnknownTrackInfo(libvlc_media_track_t track) {
        return new UnknownTrackInfo(
                track.i_codec,
                track.i_original_fourcc,
                track.i_id,
                track.i_profile,
                track.i_level,
                track.i_bitrate,
                VideoLan4J.copyNativeString(track.psz_language),
                VideoLan4J.copyNativeString(track.psz_description),
                codecDescription(TrackType.UNKNOWN, track.i_codec)
        );
    }

    private static TrackInfo getVideoTrackInfo(libvlc_media_track_t track) {
        track.u.setType(libvlc_video_track_t.class);
        track.u.read();
        return new VideoTrackInfo(
                track.i_codec,
                track.i_original_fourcc,
                track.i_id,
                track.i_profile,
                track.i_level,
                track.i_bitrate,
                VideoLan4J.copyNativeString(track.psz_language),
                VideoLan4J.copyNativeString(track.psz_description),
                track.u.video.i_width,
                track.u.video.i_height,
                track.u.video.i_sar_num,
                track.u.video.i_sar_den,
                track.u.video.i_frame_rate_num,
                track.u.video.i_frame_rate_den,
                VideoOrientation.videoOrientation(track.u.video.i_orientation),
                VideoProjection.videoProjection(track.u.video.i_projection),
                track.u.video.pose.f_yaw,
                track.u.video.pose.f_pitch,
                track.u.video.pose.f_roll,
                track.u.video.pose.f_field_of_view,
                null,
                codecDescription(TrackType.VIDEO, track.i_codec)
        );
    }

    private static TrackInfo getAudioTrackInfo(libvlc_media_track_t track) {
        track.u.setType(libvlc_audio_track_t.class);
        track.u.read();
        return new AudioTrackInfo(
                track.i_codec,
                track.i_original_fourcc,
                track.i_id,
                track.i_profile,
                track.i_level,
                track.i_bitrate,
                VideoLan4J.copyNativeString(track.psz_language),
                VideoLan4J.copyNativeString(track.psz_description),
                track.u.audio.i_channels,
                track.u.audio.i_rate,
            codecDescription(TrackType.AUDIO, track.i_codec)
        );
    }

    private static TrackInfo getTextTrackInfo(libvlc_media_track_t track) {
        track.u.setType(libvlc_subtitle_track_t.class);
        track.u.read();
        return new TextTrackInfo(
                track.i_codec,
                track.i_original_fourcc,
                track.i_id,
                track.i_profile,
                track.i_level,
                track.i_bitrate,
                VideoLan4J.copyNativeString(track.psz_language),
                VideoLan4J.copyNativeString(track.psz_description),
                VideoLan4J.copyNativeString(track.u.subtitle.psz_encoding),
                codecDescription(TrackType.TEXT, track.i_codec)
        );
    }

    private static String codecDescription(TrackType type, int codec) {
        return LibVlc.libvlc_media_get_codec_description(type.intValue(), codec);
    }

    private TrackInformation() {
    }

}