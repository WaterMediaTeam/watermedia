package org.watermedia.tools;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


public record ArgTool(String key, String value, AtomicReference<String> override) implements Supplier<String>, BooleanSupplier, IntSupplier, DoubleSupplier {

    /**
     * it is thread safe
     * @param key argument key
     */
    public ArgTool(String key) {
        this(key, System.getProperty(key), new AtomicReference<>(null));
    }

    public void override(String override) {
        this.override.set(override);
    }

    public String argument() {
        return key;
    }

    @Override
    public String get() {
        return determinate();
    }

    @Override
    public String value() {
        return determinate();
    }

    @Override
    public boolean getAsBoolean() {
        return Boolean.parseBoolean(determinate());
    }

    @Override
    public int getAsInt() {
        return Integer.parseInt(determinate());
    }

    @Override
    public double getAsDouble() {
        return Double.parseDouble(determinate());
    }

    @Override
    public String toString() {
        return "D" + key + "=" + determinate();
    }

    private String determinate() {
        String plain = override.get();
        return plain == null ? value : plain;
    }
}
