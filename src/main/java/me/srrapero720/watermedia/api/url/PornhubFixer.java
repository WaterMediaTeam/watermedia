package me.srrapero720.watermedia.api.url;

import me.srrapero720.watermedia.api.network.pornhub.PornhubUtil;
import me.srrapero720.watermedia.api.network.pornhub.VideoQuality;

import java.net.URL;
import java.util.List;

public class PornhubFixer extends NSFixer {

    @Override
    public String platform() {
        return "Pornhub";
    }

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("es.pornhub.com") || url.getHost().equals("www.pornhub.com")) && url.getPath().startsWith("/view_video.php");
    }

    @Override
    public Result patch(URL url, Quality prefQuality) throws FixingURLException {
        super.patch(url, prefQuality);
        try {
            List<VideoQuality> videos = PornhubUtil.getVideo(url.toString());
            return new Result(new URL(videos.get(0).getUri()), true, false);
        } catch (Exception e) {
            throw new FixingURLException(url.toString(), e);
        }
    }
}
