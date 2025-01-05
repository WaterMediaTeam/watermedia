package org.watermedia.api.network;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.media.MediaPlayer;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Function;

public class MRL implements Comparable<URI>, Serializable {
    public static final long NO_EXPIRATION = -1;
    private static final Map<URI, MRL> MEDIA_URIS = new HashMap<>();

    public static MRL get(File file) { return get(file.toURI()); }
    public static MRL get(URI uri) { return MEDIA_URIS.computeIfAbsent(uri, MRL::new); }
    public static MRL get(String url) {
        try {
            return MEDIA_URIS.computeIfAbsent(new URI(url), MRL::get);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL is not valid '" + url + "'", e);
        }
    }

    // instance
    private final URI uri;
    private final List<Source> sources = new ArrayList<>();
    private Metadata metadata;
    private long expires;
    private boolean patched;

    private MRL(URI uri) {
        this.uri = uri;
        this.sources.add(new Source(uri));
    }

    public URI getUri() {
        return uri;
    }

    public List<Source> getSources() {
        return List.copyOf(sources);
    }

    public boolean patched() {
        return patched && (expires == NO_EXPIRATION || System.currentTimeMillis() < expires);
    }

    public void apply(Patch patch) {
        this.sources.clear();
        this.sources.addAll(patch.sources);
        this.metadata = patch.metadata;
        this.expires = patch.expires;
        this.patched = true;
    }

    public int size() {
        return sources.size();
    }

    @Override
    public int compareTo(URI o) {
        return o.compareTo(this.uri);
    }

    public static class Source {
        private final URI uri;
        private final List<Slave> slaves;
        private final Map<MediaPlayer.Quality, URI> qualities;
        private URLConnection connection;
        private MediaPlayer.Type type;

        private URI fallbackUri;
        private MediaPlayer.Type fallbackType;
        private boolean live;

        public Source(URI uri) {
            this.uri = uri;
            this.slaves = new ArrayList<>();
            this.qualities = new HashMap<>();
        }

        public Source(URI uri, List<Slave> slaves, Map<MediaPlayer.Quality, URI> qualities) {
            this.uri = uri;
            this.slaves = slaves;
            this.qualities = qualities;
        }

        public URI fallbackUri() {
            return this.fallbackUri;
        }

        public boolean live() {
            return this.live;
        }

        public int size() {
            return qualities.isEmpty() ? 1 : qualities.size();
        }

        public URI uri(MediaContext context, MediaPlayer.Quality quality) {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(quality);
            MediaPlayer.Quality currentQuality = context.preferLowerQuality() ? quality.getBack() : quality.getNext();
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = context.preferLowerQuality() ? currentQuality.getBack() : currentQuality.getNext();
            }
            return uri == null ? this.uri : uri;
        }

        public URI highQualityUri() {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(MediaPlayer.Quality.HIGHEST);
            MediaPlayer.Quality currentQuality = MediaPlayer.Quality.HIGH;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getBack();
            }

            return uri == null ? this.uri : uri;
        }

        public URI lowerQualityUri() {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(MediaPlayer.Quality.LOWEST);
            MediaPlayer.Quality currentQuality = MediaPlayer.Quality.LOW;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getNext();
            }

            return uri == null ? this.uri : uri;
        }

        public Slave[] slaves() {
            return slaves.stream().filter(slave -> slave.type == MediaPlayer.Type.AUDIO || slave.type == MediaPlayer.Type.SUBTITLES).toArray(v -> new Slave[0]);
        }

        @Override
        public String toString() {
            return "Source{" +
                    "source=" + uri +
                    ", slaves=" + Arrays.toString(slaves.toArray(new Slave[0])) +
                    ", qualities=" + Arrays.toString(qualities.values().toArray(new URI[0])) +
                    ", type=" + type +
                    ", fallbackUri=" + fallbackUri +
                    ", fallbackType=" + fallbackType +
                    ", live=" + live +
                    '}';
        }
    }



    public static class Patch {

        private final List<Source> sources = new ArrayList<>();
        private long expires = NO_EXPIRATION;
        private Metadata metadata;

        public Patch setExpiration(long expirationTime) {
            this.expires = expirationTime;
            return this;
        }

        public Patch setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public SourceBuilder addSource() {
            return new SourceBuilder();
        }

        public class SourceBuilder {
            private URI uri;
            private MediaPlayer.Type type;
            private MediaPlayer.Type fallbackType;
            private URI fallbackUri;
            private boolean isLive;
            private final Map<MediaPlayer.Quality, URI> qualities = new HashMap<>();
            private final List<Slave> slaves = new ArrayList<>();

            private SourceBuilder() {}

            public SourceBuilder addSlave(Slave slave) {
                this.slaves.add(slave);
                return this;
            }

            public SourceBuilder setUri(URI uri) {
                this.uri = uri;
                return this;
            }

            public SourceBuilder setFallbackUri(URI uri) {
                this.fallbackUri = uri;
                return this;
            }

            public SourceBuilder setIsLive(boolean live) {
                this.isLive = live;
                return this;
            }

            public SourceBuilder setType(MediaPlayer.Type type) {
                this.type = type;
                return this;
            }

            public SourceBuilder setFallbackType(MediaPlayer.Type fallbackType) {
                this.fallbackType = fallbackType;
                return this;
            }

            public SourceBuilder putQuality(MediaPlayer.Quality quality, URI uri) {
                this.qualities.put(quality, uri);
                return this;
            }

            public SourceBuilder putQualityIfAbsent(MediaPlayer.Quality quality, Function<MediaPlayer.Quality, URI> uri) {
                this.qualities.computeIfAbsent(quality, uri);
                return this;
            }

            public SourceBuilder putQualityIfAbsent(MediaPlayer.Quality quality, URI uri) {
                this.qualities.computeIfAbsent(quality, q -> uri);
                return this;
            }

            public Patch build() {
                if ((uri == null && qualities.isEmpty()))
                    throw new IllegalStateException("Uri is null and qualities is empty");

                if (uri == null) {
                    uri = qualities.values().toArray(o -> new URI[0])[0];
                }

                var source = new Source(uri, slaves, qualities);
                source.fallbackUri = this.fallbackUri;
                source.live = this.isLive;
                source.type = this.type;
                source.fallbackType = this.fallbackType;

                Patch.this.sources.add(source);
                return Patch.this;
            }
        }
    }

    public record Slave(MediaPlayer.Type type, URI slave) {

    }

    public record Metadata(String name, String author, String platform, String description, URI thumbnailURI, long duration) {

    }

    public enum Status {
        VALID,
        FETCHING,
        CACHING,
        INVALID,
    }
}
