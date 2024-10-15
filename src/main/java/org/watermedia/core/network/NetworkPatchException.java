package org.watermedia.core.network;

import org.watermedia.api.network.MRL;

import java.net.URI;

public class NetworkPatchException extends Exception {
    public NetworkPatchException(String uri, String message) {
        super("Failed to patch URI '" + uri + "'; " + message);
    }

    public NetworkPatchException(String uri, Exception e) {
        super("Failed to patch URI '" + uri + "'; " + e.getLocalizedMessage(), e);
    }

    public NetworkPatchException(URI source, String message) {
        this(source.toString(), message);
    }

    public NetworkPatchException(URI source, Exception message) {
        this(source.toString(), message);
    }

    public NetworkPatchException(MRL source, String message) {
        this(source.getUri(), message);
    }

    public NetworkPatchException(MRL source, Exception e) {
        this(source.getUri(), e);
    }
}
