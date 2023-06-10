package me.srrapero720.watermedia.api.media.compat;

import me.srrapero720.watermedia.network.KickApi;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class KickCompat extends CompatVideoUrl {

    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getHost().contains("kick.com");
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);

        if (url.getPath().contains("/video/")) {
             var call = KickApi.NET.getVideoInfo(url.getPath().replace("/video/", ""));
             try {
                 var res = call.execute();
                 if (res.isSuccessful() && res.body() != null) return res.body().url;
             } catch (Exception e) {
                 LOGGER.error("Detected Kick.com video but Compat failed to get real URL", e);
             }
        } else {
            var call = KickApi.NET.getChannelInfo(url.getPath().replace("/", ""));
            try {
                var res = call.execute();
                if (res.isSuccessful() && res.body() != null) return res.body().url;
            } catch (Exception e) {
                LOGGER.error("Detected Kick.com stream but Compat failed to get real URL", e);
            }
        }

        return null;
    }
}