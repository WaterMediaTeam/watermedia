package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface Event<T, P extends VideoPlayer> {
    void call(P p, T o);
}