package me.srrapero720.watermedia.api.networkv2.patch;

import me.srrapero720.watermedia.api.networkv2.DynamicURL;
import me.srrapero720.watermedia.api.networkv2.NetworkAPI;
import me.srrapero720.watermedia.api.networkv2.URLPatcher;
import me.srrapero720.watermedia.tools.exceptions.PatchingURLException;

public class DropboxPatch extends URLPatcher {
    @Override
    public String platform() {
        return "Dropbox";
    }

    @Override
    public boolean isValid(DynamicURL url) {
        String q;
        return url.asURL().getHost().contains("dropbox.com") && ((q = url.asURL().getQuery()) != null) && q.contains("dl=0");
    }

    @Override
    public DynamicURL patch(DynamicURL url, Quality prefQuality) throws PatchingURLException {
        super.patch(url, prefQuality);
        try {
            return NetworkAPI.createDynamicUrl(url.toString().replace("dl=0", "dl=1"), false, false);
        } catch (Exception e) {
            throw new PatchingURLException(url.getSource(), e);
        }
    }
}
