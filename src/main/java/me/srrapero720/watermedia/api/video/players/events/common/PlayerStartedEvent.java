package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface PlayerStartedEvent<P extends Player> extends Event<PlayerStartedEvent.EventData, P> {
    record EventData() {}
}
