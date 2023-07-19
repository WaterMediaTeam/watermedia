package me.srrapero720.watermedia.api.url.patch;

import me.srrapero720.watermedia.api.url.URLPatch;

import java.net.URL;

public class DropboxPatch extends URLPatch {
    @Override
    public boolean isValid(URL url) {
        String q;
        return url.getHost().contains("dropbox.com") && ((q = url.getQuery()) != null) && q.contains("dl=0");
    }

    @Override
    public String patch(URL url) throws PatchingUrlException {
        super.patch(url);
        return url.toString().replace("dl=0", "dl=1");
    }
}
