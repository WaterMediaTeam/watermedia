package me.srrapero720.watermedia.api.players.events;

import me.srrapero720.watermedia.api.players.VideoPlayer;
import me.srrapero720.watermedia.api.players.AbstractPlayer;

public class PlayerStateEvent extends Event {


    public static class Error extends PlayerStateEvent {
        public Error(AbstractPlayer player) {

        }
    }

    public static class Prepare extends PlayerStateEvent {
        public Prepare(VideoPlayer videoPlayer) {

        }
    }

    public static class Started extends PlayerStateEvent {

    }

    public static class Ready extends PlayerStateEvent {
        public Ready(AbstractPlayer player) {

        }
    }
}
