package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

import java.net.URL;

public interface MediaFinishEvent<P extends Player> extends Event<MediaFinishEvent.EventData, P> {
    record EventData(URL url) {}
}
