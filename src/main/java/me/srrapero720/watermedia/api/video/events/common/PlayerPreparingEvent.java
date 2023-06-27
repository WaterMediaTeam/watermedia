package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface PlayerPreparingEvent<P extends VPlayer> extends Event<PlayerPreparingEvent.EventData, P> {
    record EventData() {}
}
