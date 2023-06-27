package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface MediaStoppedEvent<P extends VPlayer> extends Event<MediaStoppedEvent.EventData, P> {
    record EventData(long time) {}
}
