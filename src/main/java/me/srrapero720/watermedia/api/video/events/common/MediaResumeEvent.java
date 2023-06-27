package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface MediaResumeEvent<P extends VPlayer> extends Event<MediaResumeEvent.EventData, P> {
    record EventData(long time) {}
}
