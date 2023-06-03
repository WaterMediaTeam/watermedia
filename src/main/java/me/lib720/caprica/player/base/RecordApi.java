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

/**
 * Behaviour pertaining to recording media.
 */
public final class RecordApi extends BaseApi {

    RecordApi(MediaPlayer mediaPlayer) {
        super(mediaPlayer);
    }

    /**
     * Start recording.
     * <p>
     * A {@link MediaPlayerEventListener#recordChanged(MediaPlayer, boolean, String)} event will be raised.
     *
     * @param outputPath name of the directory to save the recording to
     */
    public void startRecording(String outputPath) {
        LibVlc.libvlc_media_player_record(mediaPlayerInstance, 1, outputPath);
    }

    /**
     * Stop recording.
     */
    public void stopRecording() {
        LibVlc.libvlc_media_player_record(mediaPlayerInstance, 0, null);
    }
}
