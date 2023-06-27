package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface PlayerStartedEvent<P extends VPlayer> extends Event<PlayerStartedEvent.EventData, P> {
    record EventData() {}
}
