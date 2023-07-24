package me.srrapero720.watermedia.api.video.event.data;

import me.srrapero720.watermedia.api.video.VideoLANPlayer;
import me.srrapero720.watermedia.api.video.VideoPlayer;

public class MediaBufferingEvent extends Event {
    public static class Start extends MediaBufferingEvent {

    }

    public static class Progress extends MediaBufferingEvent {

        public Progress(VideoPlayer videoPlayer, float newCache) {

        }
    }

    public static class End extends MediaBufferingEvent {

        public End(VideoPlayer videoPlayer) {

        }
    }
}
