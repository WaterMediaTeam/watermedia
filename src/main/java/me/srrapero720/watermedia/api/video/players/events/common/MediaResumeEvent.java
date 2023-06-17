package me.srrapero720.watermedia.api.video.players.events.common;

import me.srrapero720.watermedia.api.video.players.Player;

public interface MediaResumeEvent<P extends Player> extends Event<MediaResumeEvent.EventData, P> {
    record EventData(long time) {}
}
