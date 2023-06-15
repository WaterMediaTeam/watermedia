package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.video.players.VideoLanPlayer;
import me.srrapero720.watermedia.api.video.players.events.common.MediaTimeChanged;

public class Testo {
    public static void t() {
        var player = new VideoLanPlayer(null, null, null);
        player.addEventListener((MediaTimeChanged<VideoLanPlayer>) (player1, oldTime, newTime) -> {

        });

        player.start("urk");

    }
}
