package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.IEventBus;

public class FMLJavaModLoadingContext {
    public static FMLJavaModLoadingContext get() {
        return new FMLJavaModLoadingContext();
    }
    public IEventBus getModEventBus() {
        return (IEventBus) new Object();
    }
}
