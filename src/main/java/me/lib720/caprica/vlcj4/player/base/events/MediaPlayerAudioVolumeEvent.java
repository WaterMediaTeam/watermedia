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

package me.lib720.caprica.vlcj4.player.base.events;

import me.lib720.caprica.vlcj4.binding.internal.libvlc_event_t;
import me.lib720.caprica.vlcj4.binding.internal.media_player_audio_volume;
import me.lib720.caprica.vlcj4.player.base.MediaPlayer;
import me.lib720.caprica.vlcj4.player.base.MediaPlayerEventListener;

/**
 * Encapsulation of a media player audio volume event.
 */
final class MediaPlayerAudioVolumeEvent extends MediaPlayerEvent {

    private final float volume;

    MediaPlayerAudioVolumeEvent(MediaPlayer mediaPlayer, libvlc_event_t event) {
        super(mediaPlayer);
        this.volume = ((media_player_audio_volume) event.u.getTypedValue(media_player_audio_volume.class)).volume;
    }

    @Override
    public void notify(MediaPlayerEventListener listener) {
        listener.volumeChanged(mediaPlayer, volume);
    }

}
