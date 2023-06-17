package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public class PlayerBuffer {
    public interface ProgressEvent<P extends Player> extends Event<EventProgressData, P> {}
    public interface EndEvent<P extends Player> extends Event<EventEndData, P> { }

    public record EventProgressData(float bufferProgress) {}
    public record EventEndData() {}
}
