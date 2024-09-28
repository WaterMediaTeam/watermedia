package me.srrapero720.watermedia.api.media;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaURI implements Comparable<URI>, Serializable {
    public static final int NO_EXPIRES = -1;
    private static final Map<URI, MediaURI> ACTIVE_SOURCES = new HashMap<>();

    // instance
    private final URI uri;
    private List<Source>

    @Override
    public int compareTo(URI o) {
        return ;
    }

    public static class Source {
    }

    public static class Quality {
    }
}
