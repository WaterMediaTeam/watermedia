package org.watermedia.core.tools;

import java.util.function.Supplier;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class ArgTool implements Supplier<String>, BooleanSupplier, IntSupplier, DoubleSupplier {
    private final String key;
    private final String value;
    private String override;
    public ArgTool(String key) {
        this.key = key;
        this.value = System.getProperty(key);
    }

    public void override(String override) {
        this.override = override;
    }

    public String getKey() {
        return key;
    }

    public String argument() {
        return key;
    }

    @Override
    public String get() {
        return determinate();
    }

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
        return override != null ? override : value;
    }
}
