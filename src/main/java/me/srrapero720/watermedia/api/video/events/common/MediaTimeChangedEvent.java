package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface MediaTimeChangedEvent<P extends VideoPlayer> extends Event<MediaTimeChangedEvent.EventData, P> {
    record EventData(long beforeTime, long afterTime) {}
}
