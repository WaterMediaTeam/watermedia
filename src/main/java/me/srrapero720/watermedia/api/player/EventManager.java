package me.srrapero720.watermedia.api.player;

import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.api.player.events.Event;
import me.srrapero720.watermedia.api.player.events.SubscribePlayerEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class EventManager {
    private final List<Event.Listener<? extends Event>> listeners = new ArrayList<>();

    public void addEventListener(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(SubscribePlayerEvent.class) != null) listeners.add(eventData -> ThreadCore.trySimple(() -> method.invoke(object, eventData)));
        }
    }
    public <T extends Event> void addEventListener(Event.Listener<T> listener) { listeners.add(listener); }
    public <T extends Event> void removeEventListener(Event.Listener<T> listener) { listeners.remove(listener); }

    @SuppressWarnings("unchecked")
    protected  <T extends Event> void post(T eventData) {
        for (Event.Listener<? extends Event> listener : listeners) {
            if (listener.getClass().equals(eventData.getClass()) || listener.getClass().isAssignableFrom(eventData.getClass())) {
                ((Event.Listener<T>) listener).onEvent(eventData);
            }
        }
    }
}
