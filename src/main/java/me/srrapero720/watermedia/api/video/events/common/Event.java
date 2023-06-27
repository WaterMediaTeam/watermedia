package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface Event<T, P extends VPlayer> {
    void call(P p, T o);
}