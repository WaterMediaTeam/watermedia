package me.srrapero720.watermedia.api.url.patch;

import me.srrapero720.watermedia.api.url.URLPatch;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class OnedrivePatch extends URLPatch {
    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getHost().endsWith(".ms");
    }

    @Override
    public String patch(@NotNull URL url) throws PatchingUrlException {
        return super.patch(url);

    }
}
