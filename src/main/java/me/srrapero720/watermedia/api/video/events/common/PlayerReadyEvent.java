package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface PlayerReadyEvent<P extends VideoPlayer> extends Event<PlayerReadyEvent.EventData, P> {
    record EventData() {}
}
