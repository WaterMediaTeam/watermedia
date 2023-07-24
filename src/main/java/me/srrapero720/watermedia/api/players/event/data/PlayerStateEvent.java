package me.srrapero720.watermedia.api.players.event.data;

import me.srrapero720.watermedia.api.players.VideoPlayer;
import me.srrapero720.watermedia.api.players.Player;

public class PlayerStateEvent extends Event {


    public static class Error extends PlayerStateEvent {
        public Error(Player player, RuntimeException exception) {

        }
    }

    public static class Prepare extends PlayerStateEvent {
        public Prepare(VideoPlayer videoPlayer) {

        }
    }

    public static class Started extends PlayerStateEvent {

    }

    public static class Ready extends PlayerStateEvent {
        public Ready(Player player) {

        }
    }
}
