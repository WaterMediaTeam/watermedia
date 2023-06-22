package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaPauseEvent<P extends Player> extends Event<MediaPauseEvent.EventData, P> {
    record EventData(long time) {}
}
