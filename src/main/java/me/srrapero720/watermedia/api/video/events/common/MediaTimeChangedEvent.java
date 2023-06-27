package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface MediaTimeChangedEvent<P extends VPlayer> extends Event<MediaTimeChangedEvent.EventData, P> {
    record EventData(long beforeTime, long afterTime) {}
}
