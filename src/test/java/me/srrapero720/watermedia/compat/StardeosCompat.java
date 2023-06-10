package me.srrapero720.watermedia.compat;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.api.media.compat.CompatVideoUrl;
import me.srrapero720.watermedia.api.network.StardeosApi;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

// Planned
public class StardeosCompat extends CompatVideoUrl {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern regex = Pattern.compile("\\/video\\/([a-zA-Z0-9]+)$");

    public static void main(String[] args) throws MalformedURLException {
        LOGGER.info(new StardeosCompat().build(new URL("https://stardeos.com/video/63fb79912ada8aa4fafc1b6a")));
    }

    @Override
    public boolean isValid(@NotNull URL url) { return url.toString().contains("stardeos.com/video/"); }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);

        // OBTEN EL VIDEO ID
        var matcher = regex.matcher(url.toString());
        if (!matcher.find()) return null;
        var videoId = matcher.group(1);

        // INICIA CONEXION CON EL SERVIDOR EXTERNO
        var call = StardeosApi.NET.getVideoInfo(videoId);
        try {
            var res = call.execute();
            if (res.isSuccessful() && res.body() != null) {
                for (var file: res.body().files) {
                    if (file.fileUrl.contains("high") && !file.alert) return file.fileUrl;
                }
            } else {
                LOGGER.error("Detected Stardeos.com but request to get real URL wasn't successfuly \n\nStatus code: {}\n\nResponse: {}", res.code(), res.body());
            }
        } catch (Exception e) {
            LOGGER.error("Detected Stardeos.com but request to get real URL wasn't successfuly");
        }

        return null;
    }
}
