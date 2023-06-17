package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface Event<T, P extends Player> {
    void call(P p, T o);
}