package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;

public class DrivePatch extends AbstractPatch {
    private static final String API_KEY = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo";
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s";

    @Override
    public String platform() {
        return "Google Drive";
    }

    @Override
    public boolean validate(MediaURI source) {
        var host = source.getUri().getHost();
        var path = source.getUri().getPath();
        return host.equals("drive.google.com") && path.startsWith("/file/d/");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        var uri = source.getUri();
        final int start = uri.getPath().indexOf("/file/d/") + 4;

        int end = uri.getPath().indexOf('/', start);
        if (end == -1) end = uri.getPath().length();
        String fileID = uri.getPath().substring(start, end);

        return null;
    }
}
