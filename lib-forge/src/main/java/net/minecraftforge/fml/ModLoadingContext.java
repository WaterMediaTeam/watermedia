package net.minecraftforge.fml;

import java.util.function.Supplier;

public class ModLoadingContext {
    public static ModLoadingContext get() {
        return new ModLoadingContext();
    }

    public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {}
//    public <T extends Object & IExtensionPoint<T>> void registerExtensionPoint(Class<? extends IExtensionPoint<T>> point, Supplier<T> extension) {
//    }
}
