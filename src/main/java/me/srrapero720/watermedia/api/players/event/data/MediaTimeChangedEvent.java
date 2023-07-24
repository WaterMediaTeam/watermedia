package me.srrapero720.watermedia.api.players.event.data;

import me.srrapero720.watermedia.api.players.Player;

public class MediaTimeChangedEvent extends Event {
    public final long old;
    public final long fresh;
    public MediaTimeChangedEvent(Player player, long old, long fresh) {
        super();
        this.old = old;
        this.fresh = fresh;
    }
}
