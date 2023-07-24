package me.srrapero720.watermedia.api.video.event.data;

import me.srrapero720.watermedia.api.video.VideoPlayer;

public class MediaTimeChangedEvent extends Event {
    public final long old;
    public final long fresh;
    public MediaTimeChangedEvent(VideoPlayer player,  long old, long fresh) {
        super();
        this.old = old;
        this.fresh = fresh;
    }
}
