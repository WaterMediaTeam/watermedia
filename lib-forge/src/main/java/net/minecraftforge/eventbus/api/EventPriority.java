package net.minecraftforge.eventbus.api;

public enum EventPriority implements IEventListener {
    HIGHEST, //First to execute
    HIGH,
    NORMAL,
    LOW,
    LOWEST; //Last to execute


    @Override
    public void invoke(Event event) {
        throw new IllegalStateException("Stub!");
    }
}