package me.srrapero720.watermedia.api.uri;

public final class MediaMetadata {
    public String name;
    public String description;
    public String author;

    private boolean local = false;
    private boolean cached = false;
    private boolean video = false;
    private boolean stream = false;
    private boolean exists = false;
}
