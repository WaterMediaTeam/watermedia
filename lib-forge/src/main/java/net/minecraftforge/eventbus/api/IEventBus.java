package net.minecraftforge.eventbus.api;

import java.util.function.Consumer;

public interface IEventBus {
//    <T extends Event> void addListener(Consumer<T> consumer);

    <T extends Event> void addListener(Consumer<T> tConsumer);
}
