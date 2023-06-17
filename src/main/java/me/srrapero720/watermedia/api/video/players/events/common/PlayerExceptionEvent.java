package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface PlayerExceptionEvent<P extends Player> extends Event<PlayerExceptionEvent.EventData, P>  {
    record EventData(Throwable t) {}
}
