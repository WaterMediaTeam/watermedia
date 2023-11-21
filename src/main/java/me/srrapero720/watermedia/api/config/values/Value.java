package me.srrapero720.watermedia.api.config.values;

public interface Value<T> {

    T get();

    void set(T value);

    String getStringValue();
}