package me.srrapero720.watermedia.compat;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.api.media.compat.CompatVideoUrl;
import me.srrapero720.watermedia.api.network.KickApi;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class KickCompat extends CompatVideoUrl {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void main(String[] args) throws MalformedURLException {
        LOGGER.info(new KickCompat().build(new URL("https://kick.com/lornexia")));
        LOGGER.info(new KickCompat().build(new URL("https://kick.com/video/af9f91d4-9cbc-4395-8f35-9ef9f245fb8c")));
    }


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