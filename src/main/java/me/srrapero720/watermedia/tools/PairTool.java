package me.srrapero720.watermedia.tools;

import java.util.function.Supplier;

public class PairTool<K, V> implements Supplier<V> {

    private final K left;
    private final V right;
    public PairTool(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public K getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }

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
