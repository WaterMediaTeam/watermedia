package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public class PlayerBuffer {
    public interface ProgressEvent<P extends VideoPlayer> extends Event<EventProgressData, P> {}
    public interface EndEvent<P extends VideoPlayer> extends Event<EventEndData, P> { }

    public record EventProgressData(float bufferProgress) {}
    public record EventEndData() {}
}
