package net.minecraftforge.fml;

import java.util.Objects;
import java.util.function.BiFunction;

public class ExtensionPoint<T> {
    public static final ExtensionPoint<BiFunction<Object, Object, Object>> CONFIGGUIFACTORY = new ExtensionPoint<>();
    public static final ExtensionPoint<BiFunction<Object, Objects, Object>> RESOURCEPACK = new ExtensionPoint<>();
    public static final ExtensionPoint<Object> DISPLAYTEST = new ExtensionPoint<>();
}