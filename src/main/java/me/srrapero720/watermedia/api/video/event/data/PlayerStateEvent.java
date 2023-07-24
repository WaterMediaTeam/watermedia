package me.srrapero720.watermedia.api.video.event.data;

import me.srrapero720.watermedia.api.video.VideoLANPlayer;
import me.srrapero720.watermedia.api.video.VideoPlayer;

public class PlayerStateEvent extends Event {


    public static class Error extends PlayerStateEvent {
        public Error(VideoPlayer videoPlayer, RuntimeException exception) {

        }
    }

    public static class Prepare extends PlayerStateEvent {
        public Prepare(VideoLANPlayer videoLANPlayer) {

        }
    }

    public static class Started extends PlayerStateEvent {

    }

    public static class Ready extends PlayerStateEvent {
        public Ready(VideoPlayer videoPlayer) {

        }
    }
}
