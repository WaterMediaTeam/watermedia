package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

import java.net.URL;

public interface MediaFinishEvent<P extends VPlayer> extends Event<MediaFinishEvent.EventData, P> {
    record EventData(URL url) {}
}
