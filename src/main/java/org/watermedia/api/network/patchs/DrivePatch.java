package org.watermedia.api.network.patchs;

import java.net.URI;

public class DrivePatch extends AbstractPatch {
    private static final String API_KEY = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo";
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s";

    @Override
    public String platform() {
        return "Google Drive";
    }

    @Override
    public boolean isValid(URI uri) {
        return uri.getHost().equals("drive.google.com") && uri.getPath().startsWith("/file/d/");
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);
        try {

            // PATH GETTER
            final int start = uri.getPath().indexOf("/file/d/") + 8;
            int end = uri.getPath().indexOf('/', start);
            if (end == -1) end = uri.getPath().length();
            String fileID = uri.getPath().substring(start, end);

            return new Result(new URI(String.format(API_URL, fileID, API_KEY)), false, false);
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}