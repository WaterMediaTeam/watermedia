package me.srrapero720.watermedia.api.player.videolan;

import me.srrapero720.watermedia.api.player.PlayerAPI;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;

/**
 * Player variant with NO VIDEO
 */
public class MusicPlayer extends BasePlayer {


    public MusicPlayer(MediaPlayerFactory factory) {
        super(factory, null, null);
    }

    public MusicPlayer() {
        super(PlayerAPI.getFactorySoundOnly(), null, null);
    }
}