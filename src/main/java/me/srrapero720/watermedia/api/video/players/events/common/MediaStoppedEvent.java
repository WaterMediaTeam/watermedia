package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaStoppedEvent<P extends Player> extends Event<MediaStoppedEvent.EventData, P> {
    record EventData(long time) {}
}
