package me.srrapero720.watermedia.api.player.old;

import me.srrapero720.watermedia.api.player.PlayerAPI;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;

public class MusicPlayer extends VLCPlayer {

    public MusicPlayer(MediaPlayerFactory factory) {
        super(factory, null, null);
    }

    public MusicPlayer() {
        super(PlayerAPI.getFactorySoundOnly(), null, null);
    }
}