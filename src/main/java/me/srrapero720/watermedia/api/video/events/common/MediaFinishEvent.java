package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

import java.net.URL;

public interface MediaFinishEvent<P extends VideoPlayer> extends Event<MediaFinishEvent.EventData, P> {
    record EventData(URL url) {}
}
