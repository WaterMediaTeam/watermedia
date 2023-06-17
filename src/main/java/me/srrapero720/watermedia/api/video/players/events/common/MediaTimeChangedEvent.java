package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaTimeChangedEvent<P extends Player> extends Event<MediaTimeChangedEvent.EventData, P> {
    record EventData(long beforeTime, long afterTime) {}
}
