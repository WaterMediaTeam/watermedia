package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaResume<T extends Player<T>> extends Event<T> {
    void call(T player, long time);
}
