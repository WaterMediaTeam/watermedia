package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface PlayerPreparingEvent<P extends VideoPlayer> extends Event<PlayerPreparingEvent.EventData, P> {
    record EventData() {}
}
