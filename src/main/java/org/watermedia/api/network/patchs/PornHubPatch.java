package org.watermedia.api.network.patchs;

import org.watermedia.api.network.patchs.pornhub.PornHubAPI;
import org.watermedia.api.network.patchs.pornhub.VideoQuality;

import java.net.URI;
import java.util.List;

public class PornHubPatch extends AbstractPatch {

    @Override
    public String platform() {
        return "Pornhub";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        String path  = uri.getPath();
        return host != null && (host.equals("es.pornhub.com") || host.equals("www.pornhub.com")) && path != null && path.startsWith("/view_video.php");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);
        try {
            List<VideoQuality> videos = PornHubAPI.getVideo(uri.toString());
            return new Result(new URI(videos.get(0).getUri()), true, false);
        } catch (Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }
}