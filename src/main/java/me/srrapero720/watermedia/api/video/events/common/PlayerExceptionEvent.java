package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface PlayerExceptionEvent<P extends VPlayer> extends Event<PlayerExceptionEvent.EventData, P>  {
    record EventData(Throwable t) {}
}
