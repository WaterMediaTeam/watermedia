package me.srrapero720.watermedia.api.players.event;

import me.srrapero720.watermedia.api.players.event.data.Event;

public interface EventListener<T extends Event> {
    void onEvent(T eventData);
}