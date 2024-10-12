package org.watermedia.api.network;

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

    public URIPatchException(MediaURI source, String message) {
        this(source.getUri(), message);
    }

    public URIPatchException(MediaURI source, Exception e) {
        this(source.getUri(), e);
    }
}
