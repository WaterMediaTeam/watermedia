package me.srrapero720.watermedia.api.network;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.Quality;
import me.srrapero720.watermedia.api.MediaType;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class MediaURI implements Comparable<URI>, Serializable {
    public static final int NO_EXPIRES = -1;
    public static final String UNKNOWN = "unknown";
    private static final Map<URI, MediaURI> ACTIVE_SOURCES = new HashMap<>();

    public static MediaURI get(MediaContext context, URI uri) {
        return ACTIVE_SOURCES.computeIfAbsent(uri, MediaURI::new);
    }

    public static MediaURI get(MediaContext context, File file) {
        return get(context, file.toURI());
    }

    public static MediaURI get(MediaContext context, String url) {
        try {
            var uri = new URI(url);
            var media = ACTIVE_SOURCES.get(uri);
            if (media == null) {
                LOGGER.warn(NetworkAPI.IT, "Mod {} is using a experimental URI creation, may experiment issues computing URIs", context.id());
                media = new MediaURI(uri);
                ACTIVE_SOURCES.put(uri, media);
                return media;
            }
            return media;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to process URI '" + url + "'", e);
        }
    }

    // instance
    private final URI uri;
    private final Source defaultSource;
    private final List<Source> sources = new ArrayList<>();
    private Metadata metadata;
    private long expires;
    private boolean patched;

    private MediaURI(URI uri) {
        this.uri = uri;
        this.defaultSource = new Source(uri);
    }

    public int size() {
        return sources.isEmpty() ? 1 : sources.size();
    }

    public Source[] getSources() {
        if (sources.isEmpty())
            return new Source[] { defaultSource };

        return sources.toArray(new Source[0]);
    }

    public boolean patched() {
        return patched;
    }

    public void apply(Patch patch) {
        this.sources.addAll(patch.sources);
        this.metadata = patch.metadata;
        patched = true;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public int compareTo(URI o) {
        return o.compareTo(this.uri);
    }

    public static class Source {
        private final URI source;
        private final List<Slave> slaves;
        private final Map<Quality, URI> qualities;
        private MediaType type;
        private URI fallbackUri;
        private MediaType fallbackType;
        private boolean live;

        public Source(URI uri) {
            this.source = uri;
            this.slaves = new ArrayList<>();
            this.qualities = new HashMap<>();
        }

        public Source(URI uri, List<Slave> slaves, Map<Quality, URI> qualities) {
            this.source = uri;
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

        public URI uri(MediaContext context, Quality quality) {
            if (qualities.isEmpty()) return this.source;

            URI uri = qualities.get(quality);
            Quality currentQuality = context.preferLowerQuality() ? quality.getBack() : quality.getNext();
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = context.preferLowerQuality() ? currentQuality.getBack() : currentQuality.getNext();
            }
            return uri == null ? this.source : uri;
        }

        public URI highQualityUri() {
            if (qualities.isEmpty()) return this.source;

            URI uri = qualities.get(Quality.HIGHEST);
            Quality currentQuality = Quality.HIGH;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getBack();
            }

            return uri == null ? this.source : uri;
        }

        public URI lowerQualityUri() {
            if (qualities.isEmpty()) return this.source;

            URI uri = qualities.get(Quality.LOWEST);
            Quality currentQuality = Quality.LOW;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getNext();
            }

            return uri == null ? this.source : uri;
        }

        public Slave[] slaves() {
            return slaves.stream().filter(slave -> slave.type == MediaType.AUDIO || slave.type == MediaType.SUBTITLES).toArray(v -> new Slave[0]);
        }
    }

    public record Slave(MediaType type, URI slave) {}

    public record Metadata(String name, String author, String platform, String description, URI thumbnailURI, long duration) {

    }

    public static class Patch {
        private final List<Source> sources = new ArrayList<>();
        private Metadata metadata;


        public Patch setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public SourceBuilder addSource() {
            return new SourceBuilder();
        }

        public class SourceBuilder {
            private URI uri;
            private MediaType type;
            private MediaType fallbackType;
            private URI fallbackUri;
            private boolean isLive;
            private final Map<Quality, URI> qualities = new HashMap<>();
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

            public SourceBuilder setType(MediaType type) {
                this.type = type;
                return this;
            }

            public SourceBuilder setFallbackType(MediaType fallbackType) {
                this.fallbackType = fallbackType;
                return this;
            }

            public SourceBuilder putQuality(Quality quality, URI uri) {
                this.qualities.put(quality, uri);
                return this;
            }

            public SourceBuilder putQualityIfAbsent(Quality quality, Function<Quality, URI> uri) {
                this.qualities.computeIfAbsent(quality, uri);
                return this;
            }

            public SourceBuilder putQualityIfAbsent(Quality quality, URI uri) {
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
}
