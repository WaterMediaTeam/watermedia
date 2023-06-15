package me.srrapero720.watermedia.api.media.players.events.common;

import me.srrapero720.watermedia.api.media.players.Player;

public interface MediaFinishEvent<T extends Player<T>> extends Event<T> {
    void call(T player);
}
