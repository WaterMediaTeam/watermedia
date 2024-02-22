package me.srrapero720.watermedia.api.networkv2;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class DynamicURL {
    private final String source;
    private final URL url;
    private final File file;
    private boolean isVideo = false;
    private boolean isStream = false;
    private boolean cached = false;
    private boolean exists = false;

    public DynamicURL(String urlOrPath) {
        if (urlOrPath.startsWith("local://")) {
            urlOrPath = urlOrPath.replace("local://", "");
            if (urlOrPath.startsWith(File.pathSeparator)) urlOrPath = urlOrPath.substring(1);
            urlOrPath = new File(urlOrPath).getAbsolutePath();
        }
        this.source = urlOrPath;
        this.url = NetworkAPI.parseUrl(urlOrPath);
        this.file = (this.url == null) ? new File(urlOrPath) : null;
    }

    public DynamicURL(final String urlOrPath, final boolean isVideo, final boolean isStream) {
        this(urlOrPath);
        this.isVideo = isVideo;
        this.isStream = isStream;
    }

    public DynamicURL(final File file) {
        this.source = file.getAbsolutePath();
        this.file = file;
        this.url = NetworkAPI.parseUrl(file.toURI());
    }

    public DynamicURL(final Path path) {
        this(path.toFile());
    }

    public String getSource() {
        return source;
    }

    public URL asURL() {
        return this.url;
    }

    public File asFile() {
        if (!isLocal()) throw new UnsupportedOperationException("DynamicURL is not a local file");
        return file;
    }

    public boolean isLocal() {
        return this.file != null;
    }

    public boolean exists() {
        return cached ? exists : (exists = this.file.exists());
    }
}
