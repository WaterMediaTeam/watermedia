package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public class PlayerBuffer {
    interface StartEvent<T extends Player<T>> extends Event<T>{
        void call(T player, float bufferProgress);
    }

    interface EndEvent<T extends Player<T>> extends Event<T>{
        void call(T player, float bufferProgress);
    }
}
