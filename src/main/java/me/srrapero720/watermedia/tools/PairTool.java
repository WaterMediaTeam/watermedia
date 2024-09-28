package me.srrapero720.watermedia.tools;

import java.util.function.Supplier;

public record PairTool<K, V>(K left, V right) implements Supplier<V> {

    public K getKey() {
        return left;
    }

    public V getValue() {
        return right;
    }

    @Override
    public V get() {
        return right;
    }
}
