package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface PlayerExceptionEvent<P extends VideoPlayer> extends Event<PlayerExceptionEvent.EventData, P>  {
    record EventData(Throwable t) {}
}
