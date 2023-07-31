package me.srrapero720.watermedia.api.players.events;

public class Event {
    public interface Listener<T extends Event> {  void onEvent(T eventData); }
}