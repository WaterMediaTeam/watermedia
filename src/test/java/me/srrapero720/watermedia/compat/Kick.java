package me.srrapero720.watermedia.compat;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.api.video.patch.AbstractURLPatch;
import me.srrapero720.watermedia.api.video.patch.util.kick.KickAPI;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class Kick extends AbstractURLPatch {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void main(String[] args) throws MalformedURLException, PatchingUrlException {
        LOGGER.info(new Kick().build(new URL("https://kick.com/kenji")));
        LOGGER.info(new Kick().build(new URL("https://kick.com/video/af9f91d4-9cbc-4395-8f35-9ef9f245fb8c")));
    }


    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getHost().contains("kick.com");
    }

    @Override
    public String build(@NotNull URL url) throws PatchingUrlException {
        super.build(url);

        if (url.getPath().contains("/video/")) {
             var call = KickAPI.NET.getVideoInfo(url.getPath().replace("/video/", ""));
             try {
                 var res = call.execute();
                 if (res.isSuccessful() && res.body() != null) return res.body().url;
             } catch (Exception e) {
                 LOGGER.error("Detected Kick.com video but Compat failed to get real URL", e);
             }
        } else {
            var call = KickAPI.NET.getChannelInfo(url.getPath().replace("/", ""));
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