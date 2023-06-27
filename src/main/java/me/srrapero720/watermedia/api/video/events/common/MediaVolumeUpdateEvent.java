package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VPlayer;

public interface MediaVolumeUpdateEvent<P extends VPlayer> extends Event<MediaVolumeUpdateEvent.EventData, P> {
    record EventData(int beforeVolume, int afterVolume) {}
}
