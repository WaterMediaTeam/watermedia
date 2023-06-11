package me.srrapero720.watermedia.api.media.players.handler;

public abstract class PlayerHandler {
    public void onPlayerStart() {}
    public void onPlayerResume() {}
    public void onPlayerPaused() {}
    public void onPlayerBuffering() {}
    public void onPlayerBufferingEnd() {}
}
