package me.srrapero720.watermedia.api.video.events.common;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public interface MediaPauseEvent<P extends VideoPlayer> extends Event<MediaPauseEvent.EventData, P> {
    class EventData {
        public final long time;
        public EventData(long time) {
            this.time = time;
        }
    }
}
