package me.srrapero720.watermedia.api.player.events;

import me.srrapero720.watermedia.api.player.MediaPlayerBase;

public class MediaTimeChangedEvent extends Event {
    public final long old;
    public final long fresh;
    public MediaTimeChangedEvent(MediaPlayerBase player, long old, long fresh) {
        super();
        this.old = old;
        this.fresh = fresh;
    }
}
