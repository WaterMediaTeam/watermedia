package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.video.players.VideoLanPlayer;
import me.srrapero720.watermedia.api.video.players.events.common.MediaTimeChangedEvent;

public class Testo {
    public static void t() {
        var player = new VideoLanPlayer(null, null, null);
        player.addEventListener((MediaTimeChangedEvent<VideoLanPlayer>) (player1, oldTime, newTime) -> {

        });

        player.start("urk");

    }
}
