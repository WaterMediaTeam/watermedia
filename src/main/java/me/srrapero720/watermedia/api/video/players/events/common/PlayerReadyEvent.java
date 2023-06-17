package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface PlayerReadyEvent<P extends Player> extends Event<PlayerReadyEvent.EventData, P> {
    record EventData() {}
}
