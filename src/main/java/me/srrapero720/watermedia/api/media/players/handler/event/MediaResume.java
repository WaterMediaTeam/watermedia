package me.srrapero720.watermedia.api.media.players.handler.event;

import me.srrapero720.watermedia.api.media.players.Player;

public interface MediaResume<T extends Player<T>> extends Event<T> {
    void call(T player, long time);
}
