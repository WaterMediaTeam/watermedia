package me.srrapero720.watermedia.api.compat;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class DriveCompat extends AbstractCompat {
    private static final String API_KEY = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo";
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s";

    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getHost().equals("drive.google.com") && url.getPath().startsWith("/file/d/");
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);
        if (CACHE.containsKey(url.toString())) return CACHE.get(url.toString());

        // PATH GETTER
        final var start = url.getPath().indexOf("/file/d/") + 8;
        int end = url.getPath().indexOf('/', start);
        if (end == -1) end = url.getPath().length();
        var fileID = url.getPath().substring(start, end);

        return String.format(API_URL, fileID, API_KEY);
    }
}
