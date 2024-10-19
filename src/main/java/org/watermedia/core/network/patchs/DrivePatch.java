package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;

import java.net.URI;

public class DrivePatch extends AbstractPatch {
    private static final String API_KEY = "AIzaSyBiFNT6TTo506kCYYwA2NHqs36TlXC1DMo"; // Get your own api key, is easy and free.
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&key=%s";
    private static final String PATH_PREFIX = "/file/d/";
    private static final String PATH_SUFFIX = "/view";

    @Override
    public String platform() {
        return "Google Drive";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MRL source) {
        var host = source.getUri().getHost();
        var path = source.getUri().getPath();
        return host.equals("drive.google.com") && path.startsWith("/file/d/");
    }

    @Override
    public void patch(MediaContext context, MRL source) throws PatchException {
        var uri = source.getUri();
        var path = uri.getPath();

        var pre = path.substring(PATH_PREFIX.length());
        var fileId = pre.substring(0, pre.length() - PATH_SUFFIX.length());

        try {
            // PATCH BUILDING
            var patch = new MRL.Patch();
            patch.addSource()
                    .setUri(new URI(String.format(API_URL, fileId, API_KEY)))
                    .build();
            source.apply(patch);
        } catch (Exception e) {
            throw new PatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }
}
