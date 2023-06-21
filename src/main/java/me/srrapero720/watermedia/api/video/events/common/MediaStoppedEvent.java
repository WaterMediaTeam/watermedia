package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface MediaStoppedEvent<P extends VideoPlayer> extends Event<MediaStoppedEvent.EventData, P> {
    record EventData(long time) {}
}
