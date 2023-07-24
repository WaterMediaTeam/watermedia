package me.srrapero720.watermedia.api.players.event.data;

import me.srrapero720.watermedia.api.players.Player;

public class MediaBufferingEvent extends Event {
    public static class Start extends MediaBufferingEvent {

    }

    public static class Progress extends MediaBufferingEvent {

        public Progress(Player player, float newCache) {

        }
    }

    public static class End extends MediaBufferingEvent {

        public End(Player player) {

        }
    }
}
