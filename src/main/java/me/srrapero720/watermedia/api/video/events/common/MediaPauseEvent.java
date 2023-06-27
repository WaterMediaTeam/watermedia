package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface MediaPauseEvent<P extends VPlayer> extends Event<MediaPauseEvent.EventData, P> {
    record EventData(long time) {}
}
