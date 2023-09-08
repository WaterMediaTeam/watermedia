package me.srrapero720.watermedia.api.url.fixers;

import java.net.URL;

public class DriveFixer extends URLFixer {
    private static final String API_KEY = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo";
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s";

    @Override
    public String platform() {
        return "Google Drive";
    }

    @Override
    public boolean isValid(URL url) {
        return url.getHost().equals("drive.google.com") && url.getPath().startsWith("/file/d/");
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);
        try {

            // PATH GETTER
            final int start = url.getPath().indexOf("/file/d/") + 8;
            int end = url.getPath().indexOf('/', start);
            if (end == -1) end = url.getPath().length();
            String fileID = url.getPath().substring(start, end);

            return new Result(new URL(String.format(API_URL, fileID, API_KEY)), false, false);
        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}