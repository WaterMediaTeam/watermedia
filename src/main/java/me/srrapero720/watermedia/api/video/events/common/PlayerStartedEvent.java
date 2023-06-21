package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface PlayerStartedEvent<P extends VideoPlayer> extends Event<PlayerStartedEvent.EventData, P> {
    record EventData() {}
}
