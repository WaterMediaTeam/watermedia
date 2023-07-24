package me.srrapero720.watermedia.api.players.event;

import me.srrapero720.watermedia.api.players.event.data.Event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<EventListener<? extends Event>> eventListeners;

    public EventManager() {
        eventListeners = new ArrayList<>();
    }

    public <T extends Event> void addEventListener(EventListener<T> listener) {
        eventListeners.add(listener);
    }

    public <T extends Event> void removeEventListener(EventListener<T> listener) {
        eventListeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void fireEvent(T eventData) {
        for (EventListener<? extends Event> listener : eventListeners) {
            if (listener.getClass().equals(eventData.getClass()) || listener.getClass().isAssignableFrom(eventData.getClass())) {
                ((EventListener<T>) listener).onEvent(eventData);
            }
        }
    }
}
