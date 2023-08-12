package me.srrapero720.watermedia.api.player.events;

import me.srrapero720.watermedia.api.player.MediaPlayerBase;

public class MediaBufferingEvent extends Event {
    public static class Start extends MediaBufferingEvent {

    }

    public static class Progress extends MediaBufferingEvent {

        public Progress(MediaPlayerBase player, float newCache) {

        }
    }

    public static class End extends MediaBufferingEvent {

        public End(MediaPlayerBase player) {

        }
    }
}
