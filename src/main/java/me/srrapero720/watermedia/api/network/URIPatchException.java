package me.srrapero720.watermedia.api.network;

import me.srrapero720.watermedia.api.uri.MediaSource;

import java.net.URI;

public class URIPatchException extends Exception {
    public URIPatchException(String uri, String message) {
        super("Failed to patch URI '" + uri + "'; " + message);
    }

    public URIPatchException(String uri, Exception e) {
        super("Failed to patch URI '" + uri + "'; " + e.getLocalizedMessage(), e);
    }

    public URIPatchException(URI source, String message) {
        this(source.toString(), message);
    }

    public URIPatchException(URI source, Exception message) {
        this(source.toString(), message);
    }

    public URIPatchException(MediaSource source, String message) {
        this(source.getUri(), message);
    }

    public URIPatchException(MediaSource source, Exception e) {
        this(source.getUri(), e);
    }
}
