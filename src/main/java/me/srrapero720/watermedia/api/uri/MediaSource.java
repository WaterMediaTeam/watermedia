package me.srrapero720.watermedia.api.uri;

import me.srrapero720.watermedia.api.MediaModContext;
import me.srrapero720.watermedia.api.MediaQuality;
import me.srrapero720.watermedia.api.MediaType;
import me.srrapero720.watermedia.api.network.NetworkAPI;

import java.io.File;
import java.net.URI;
import java.util.*;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class MediaSource {
    public static final int NO_EXPIRES = -1;
    public static final int NO_EXPIRATION_DETERMINED = -2;
    private static final Map<URI, MediaSource> ACTIVE_SOURCES = new HashMap<>();

    private final URI uri;
    private final Source source;
    private MediaType type;

    // COMPLICATED SHIT
    private final List<MediaSource> subSources = new ArrayList<>(); // real sources, uri can be a gallery
    private final Map<MediaQuality, Source> qualities = new HashMap<>(); // source qualities (if exists)
    private MediaMetadata metadata = new MediaMetadata();
    private long expirationTime = NO_EXPIRES;

    private MediaSource(URI source) {
        this.source = new Source(this.uri = source);
    }

    /**
     * Gives you the active media source count, above 2 means the source is a gallery
     * @return active source size
     */
    public int size() {
        return subSources.isEmpty() ? 1 : subSources.size();
    }

    /**
     * Creates a new {@link MediaSource} instance if absent
     * @param mrl URL/URI like of a media resource locator
     * @return method returns the cached instance corresponding to the exact MRL
     */
    public static MediaSource get(MediaModContext context, String mrl) {
        try {
            URI uri = new URI(mrl);
            MediaSource s = ACTIVE_SOURCES.get(uri);
            if (s == null) {
                LOGGER.warn(NetworkAPI.IT, "Mod {} is using a experimental URI creation, may experiment issues computing URIs", context.id());
                return new MediaSource(uri);
            }
            return s;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process URI '" + mrl + "'", e);
        }
    }

    /**
     * Creates a new {@link MediaSource} instance if absent
     * @param mrl URL/URI like of a media resource locator
     * @return method returns the cached instance corresponding to the exact MRL
     */
    public static MediaSource get(MediaModContext context, File mrl) {
        return get(context, mrl.toURI());
    }

    /**
     * Creates a new {@link MediaSource} instance if absent
     * @param mrl URL/URI like of a media resource locator
     * @return method returns the cached instance corresponding to the exact MRL
     */
    public static MediaSource get(MediaModContext context, URI mrl) {
        return ACTIVE_SOURCES.computeIfAbsent(mrl, MediaSource::new);
    }

    public URI getUri() {
        return uri;
    }

    public boolean patchable() {
        return this.expirationTime < System.currentTimeMillis();
    }

    public Patcher patcher() {
        return new Patcher(this);
    }

    public MediaSource[] getActiveMediaSources() {
        return subSources.isEmpty() ? new MediaSource[] { this } : subSources.toArray(new MediaSource[0]);
    }

    public Source getSource(MediaModContext context) {
        MediaQuality preferredQuality = context.preferredQuality();
        Source u = this.qualities.get(preferredQuality);

        if (u == null) {
            MediaQuality closestQuality = null;
            int maxDistance = Integer.MAX_VALUE;

            for (MediaQuality quality: this.qualities.keySet()) {
                int distance = Math.abs(quality.compareTo(preferredQuality));

                if (distance < maxDistance) {
                    maxDistance = distance;
                    closestQuality = quality;
                }
            }

            if (closestQuality == null) {
                return this.source;
            }

            return this.qualities.get(closestQuality);
        }

        return u;
    }

    public record Source(URI uri, Slave... slaves) {

    }
    public record Slave(URI uri, MediaType type) {

    }

    public static final class Patcher {
        private final MediaSource mediaSource;

        private Patcher(MediaSource source) {
            this.mediaSource = source;
        }

        public Patcher source(URI uri, MediaMetadata metadata) {
            return source(uri, metadata, NO_EXPIRES);
        }

        public Patcher source(URI uri, MediaMetadata metadata, long expirationTime) {
            MediaSource s = new MediaSource(uri) {
                @Override
                public MediaSource[] getActiveMediaSources() {
                    throw new UnsupportedOperationException("SubSources cannot have more sources");
                }
            };
            s.expirationTime = expirationTime;
            s.metadata = metadata;
            this.mediaSource.subSources.add(s);
            return this;
        }

        public Patcher quality(URI uri, MediaQuality quality) {
            return quality(uri, quality, new Slave[0]);
        }

        public Patcher quality(URI uri, MediaQuality quality, Slave... slaves) {
            this.mediaSource.qualities.put(quality, new Source(uri, slaves));
            return this;
        }

        public Patcher metadata(MediaMetadata metadata) {
            this.mediaSource.metadata = metadata;
            return this;
        }

        public Patcher withExpiration(long expirationTime) {
            this.mediaSource.expirationTime = expirationTime;
            return this;
        }
    }
}
