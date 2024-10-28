package org.watermedia.api.player.videolan;

import org.watermedia.api.player.PlayerAPI;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;

/**
 * Player variant with NO VIDEO
 */
public class MusicPlayer extends BasePlayer {


    public MusicPlayer(MediaPlayerFactory factory) {
        super(factory, null, null, null);
    }

    public MusicPlayer() {
        super(PlayerAPI.getFactorySoundOnly(), null, null, null);
    }
}