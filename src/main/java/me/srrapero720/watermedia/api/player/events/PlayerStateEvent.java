package me.srrapero720.watermedia.api.player.events;

import me.srrapero720.watermedia.api.player.MediaPlayerBase;

public class PlayerStateEvent extends Event {


    public static class Error extends PlayerStateEvent {
        public Error(MediaPlayerBase player) {

        }
    }

    public static class Prepare extends PlayerStateEvent {
        public Prepare(MediaPlayerBase videoPlayer) {

        }
    }

    public static class Started extends PlayerStateEvent {

    }

    public static class Ready extends PlayerStateEvent {
        public Ready(MediaPlayerBase player) {

        }
    }
}
