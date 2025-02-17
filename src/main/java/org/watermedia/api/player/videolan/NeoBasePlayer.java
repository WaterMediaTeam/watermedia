package org.watermedia.api.player.videolan;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.videolan4j.binding.internal.*;
import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;

import java.net.URI;

class NeoBasePlayer {
    private static final Marker IT = MarkerManager.getMarker(NeoBasePlayer.class.getSimpleName());

    // VLC
    protected libvlc_media_player_t player;
    protected libvlc_instance_t instance;


    // PLAYER
    protected URI url;
    protected URI audioUrl;

    public NeoBasePlayer(MediaPlayerFactory factory) {
//        this.player = LibVlc.libvlc_media_list_player_get_media_player(factory);
    }
}
