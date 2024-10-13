package org.watermedia.api.network;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.Quality;
import me.srrapero720.watermedia.api.MediaType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Function;

public class MediaURI implements Comparable<URI>, Serializable {
    private static final Map<URI, MediaURI> MEDIA_URIS = new HashMap<>();

    public static MediaURI get(File file) { return get(file.toURI()); }
    public static MediaURI get(URI uri) { return MEDIA_URIS.computeIfAbsent(uri, MediaURI::new); }
    public static MediaURI get(String url) {
        try {
            return MEDIA_URIS.computeIfAbsent(new URI(url), MediaURI::get);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL is not valid '" + url + "'", e);
        }
    }

    // instance
    private final URI uri;
    private final List<Source> sources = new ArrayList<>();
    private final List<MediaContext> usages = new ArrayList<>();
    private Metadata metadata;
    private long expires;
    private boolean patched;

    private MediaURI(URI uri) {
        this.uri = uri;
        this.sources.add(new Source(uri));
    }

    public MediaURI addUsage(MediaContext context) {
        this.usages.add(context);
        return this;
    }

    public MediaURI removeUsage(MediaContext context) {
        this.usages.remove(context);
        return this;
    }

    public int usages() {
        return this.usages.size();
    }

    public boolean hasUsages() {
        return !this.usages.isEmpty();
    }

    public URI getUri() {
        return uri;
    }

    public URLConnection openConnection(Quality quality) {
        this.getSources()
    }

    public Source[] getSources() {
        return sources.toArray(new Source[0]);
    }

    public boolean patched() {
        return patched;
    }

    public void apply(Patch patch) {
        this.sources.clear();
        this.sources.addAll(patch.sources);
        this.metadata = patch.metadata;
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
        private final Map<Quality, URI> qualities;
        private URLConnection connection;
        private MediaType type;

        private URI fallbackUri;
        private MediaType fallbackType;
        private boolean live;

        public Source(URI uri) {
            this.uri = uri;
            this.slaves = new ArrayList<>();
            this.qualities = new HashMap<>();
        }

        public Source(URI uri, List<Slave> slaves, Map<Quality, URI> qualities) {
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

        public InputStreamc openConnection(MediaContext context, Quality quality) throws IOException {
            if (this.connection != null) {
                var in = this.connection.getInputStream();
                in.mark(Integer.MAX_VALUE);
                return this.connection; // TODO: ensure connection is always working despite begin timeout
            }
            return this.connection = this.uri(context, quality).toURL().openConnection();
        }

        public int size() {
            return qualities.isEmpty() ? 1 : qualities.size();
        }

        public URI uri(MediaContext context, Quality quality) {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(quality);
            Quality currentQuality = context.preferLowerQuality() ? quality.getBack() : quality.getNext();
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = context.preferLowerQuality() ? currentQuality.getBack() : currentQuality.getNext();
            }
            return uri == null ? this.uri : uri;
        }

        public URI highQualityUri() {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(Quality.HIGHEST);
            Quality currentQuality = Quality.HIGH;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getBack();
            }

            return uri == null ? this.uri : uri;
        }

        public URI lowerQualityUri() {
            if (qualities.isEmpty()) return this.uri;

            URI uri = qualities.get(Quality.LOWEST);
            Quality currentQuality = Quality.LOW;
            while (uri == null && currentQuality != null) {
                uri = qualities.get(currentQuality);
                currentQuality = currentQuality.getNext();
            }

            return uri == null ? this.uri : uri;
        }

        public Slave[] slaves() {
            return slaves.stream().filter(slave -> slave.type == MediaType.AUDIO || slave.type == MediaType.SUBTITLES).toArray(v -> new Slave[0]);
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
