package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface MediaResumeEvent<P extends VideoPlayer> extends Event<MediaResumeEvent.EventData, P> {
    record EventData(long time) {}
}
