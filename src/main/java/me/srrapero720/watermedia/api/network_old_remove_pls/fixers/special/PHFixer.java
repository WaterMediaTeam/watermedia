package me.srrapero720.watermedia.api.network_old_remove_pls.fixers.special;

import me.srrapero720.watermedia.api.network_old_remove_pls.fixers.URLFixer;
import me.srrapero720.watermedia.api.network_old_remove_pls.pornhub.PornHubAPI;
import me.srrapero720.watermedia.api.network_old_remove_pls.pornhub.VideoQuality;

import java.net.URL;
import java.util.List;

public class PHFixer extends SpecialFixer {

    @Override
    public String platform() {
        return "Pornhub";
    }

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("es.pornhub.com") || url.getHost().equals("www.pornhub.com")) && url.getPath().startsWith("/view_video.php");
    }

    @Override
    public URLFixer.Result patch(URL url, URLFixer.Quality prefQuality) throws URLFixer.FixingURLException {
        super.patch(url, prefQuality);
        try {
            List<VideoQuality> videos = PornHubAPI.getVideo(url.toString());
            return new URLFixer.Result(videos.get(0).getUri(), true, false);
        } catch (Exception e) {
            throw new URLFixer.FixingURLException(url.toString(), e);
        }
    }
}