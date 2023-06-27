package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface PlayerReadyEvent<P extends VPlayer> extends Event<PlayerReadyEvent.EventData, P> {
    record EventData() {}
}
