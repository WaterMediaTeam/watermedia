package me.srrapero720.watermedia.api.player.events;

import me.srrapero720.watermedia.api.player.VideoPlayer;
import me.srrapero720.watermedia.api.player.AbstractPlayer;

public class PlayerStateEvent extends Event {


    public static class Error extends PlayerStateEvent {
        public Error(AbstractPlayer player) {

        }
    }

    public static class Prepare extends PlayerStateEvent {
        public Prepare(AbstractPlayer videoPlayer) {

        }
    }

    public static class Started extends PlayerStateEvent {

    }

    public static class Ready extends PlayerStateEvent {
        public Ready(AbstractPlayer player) {

        }
    }
}
