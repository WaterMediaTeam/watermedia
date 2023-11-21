package me.srrapero720.watermedia.api.player.vlc;

import me.srrapero720.watermedia.api.player.PlayerAPI;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.callback.AudioCallback;


public class MusicPlayer extends SimplePlayer {
    public MusicPlayer(MediaPlayerFactory factory) {
        super();
        this.init(factory, null, null);
    }

    // EXPERIMENTAL
    public MusicPlayer(AudioCallback audioCallback) {
        super();
        this.init(PlayerAPI.getFactoryMem(), null, null);
        this.raw().mediaPlayer().audio().callback("ogg", 48000, 2, audioCallback);

    }
}