package net.minecraftforge.fml;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ExtensionPoint<T> {
    public static final ExtensionPoint<BiFunction<Object, Object, Object>> CONFIGGUIFACTORY = new ExtensionPoint<>();
    public static final ExtensionPoint<BiFunction<Object, Objects, Object>> RESOURCEPACK = new ExtensionPoint<>();
    public static final ExtensionPoint<Pair<Supplier<String>, BiPredicate<String, Boolean>>> DISPLAYTEST = new ExtensionPoint<>();
}