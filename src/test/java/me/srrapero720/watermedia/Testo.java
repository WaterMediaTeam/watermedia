package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.video.players.VideoLanPlayer;
import me.srrapero720.watermedia.api.video.players.events.common.MediaTimeChangedEvent;
import me.srrapero720.watermedia.api.video.players.events.common.PlayerReadyEvent;

public class Testo {
    public static void t() {
        var player = new VideoLanPlayer(null, null, null);

        player.start("urk");

    }
}
