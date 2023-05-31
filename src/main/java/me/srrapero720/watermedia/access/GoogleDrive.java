package me.srrapero720.watermedia.access;

import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;

import java.net.URL;

public class GoogleDrive {
    // Hey there, good looking?
    // Probably you want to use this, but no do that
    // You can make your own free key... or even use WATERMeDIA (contains the same key but you need to use as dependency)
    // help me sharing my mod and i will help you making more fancy code to steal, FOR FREE!
    private static final String apiKey = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo";
    private final String URI;

    public GoogleDrive(String googleDriveLink) { this.URI = constructApiUrl(googleDriveLink, apiKey); }
    public String get() { return URI; }

    public static String constructApiUrl(String googleDriveLink, String apiKey) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            var url = new URL(googleDriveLink);
            return isValidGoogleDriveUrl(url)
                    ? String.format("https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s", extractFileIdFromUrl(url), apiKey)
                    : defaultVar;
        }, Throwable::printStackTrace, null);
    }

    // UTIL
    private static boolean isValidGoogleDriveUrl(URL url) {
        return url.getHost().equals("drive.google.com") && url.getPath().startsWith("/file/d/");
    }

    private static String extractFileIdFromUrl(URL url) {
        String path = url.getPath();
        int startIndex = path.indexOf("/file/d/") + 8;
        int endIndex = path.indexOf('/', startIndex);
        if (endIndex == -1) {
            endIndex = path.length();
        }
        return path.substring(startIndex, endIndex);
    }
}