package me.srrapero720.watermedia.api.video.event;

import me.srrapero720.watermedia.api.video.event.data.Event;

public interface EventListener<T extends Event> {
    void onEvent(T eventData);
}