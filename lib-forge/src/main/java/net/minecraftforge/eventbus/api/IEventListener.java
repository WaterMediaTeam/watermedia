
package net.minecraftforge.eventbus.api;

public interface IEventListener {
    void invoke(Event var1);

    default String listenerName() {
        return this.getClass().getName();
    }
}