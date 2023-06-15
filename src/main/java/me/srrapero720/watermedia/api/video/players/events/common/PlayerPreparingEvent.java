package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface PlayerPreparingEvent<T extends Player<T>> extends Event<T> {
    void call(T player, String url);
}
