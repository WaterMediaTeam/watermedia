package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaVolumeUpdateEvent<P extends Player> extends Event<MediaVolumeUpdateEvent.EventData, P> {
    record EventData(int beforeVolume, int afterVolume) {}
}
