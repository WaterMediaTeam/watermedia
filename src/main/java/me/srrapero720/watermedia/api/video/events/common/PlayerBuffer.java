package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public class PlayerBuffer {
    public interface ProgressEvent<P extends VPlayer> extends Event<EventProgressData, P> {}
    public interface EndEvent<P extends VPlayer> extends Event<EventEndData, P> { }

    public record EventProgressData(float bufferProgress) {}
    public record EventEndData() {}
}
