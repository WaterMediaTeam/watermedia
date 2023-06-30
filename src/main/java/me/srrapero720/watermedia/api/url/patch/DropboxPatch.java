package me.srrapero720.watermedia.api.url.patch;

import me.srrapero720.watermedia.api.url.URLPatch;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class DropboxPatch extends URLPatch {
    @Override
    public boolean isValid(@NotNull URL url) {
        String q;
        return url.getHost().contains("dropbox.com") && ((q = url.getQuery()) != null) && q.contains("dl=0");
    }

    @Override
    public String patch(@NotNull URL url) throws PatchingUrlException {
        super.patch(url);
        return url.toString().replace("dl=0", "dl=1");
    }
}
