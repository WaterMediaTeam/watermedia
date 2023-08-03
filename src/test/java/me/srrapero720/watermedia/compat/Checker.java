package me.srrapero720.watermedia.compat;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.url.KickFixer;
import me.srrapero720.watermedia.api.url.TwitchFixer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.URL;

public class Checker {
//    private static final Logger LOGGER = LoggerFactory.getLogger("Checker");
//    private static final Marker IT = MarkerFactory.getMarker("TEST");
    public static void main(String ...args) throws Exception {
//        String url = WaterMediaAPI.urlPatch("https://1drv.ms/i/s!AoHO9f0CSCtxozX0Y6Noqo7Dg8CR?e=zPU5Ba");
//        LOGGER.info(IT, "Url from OneDrive {}", url);
//
//        url = WaterMediaAPI.urlPatch("https://www.youtube.com/watch?v=hsXeFqj5p7Q&list=RDYIC1aK6CleE&index=6&ab_channel=Diversity");
//        LOGGER.info(IT, "Url from Youtube {}", url);

        URL url = new KickFixer().patch(new URL("https://kick.com/javioliveira"));
        System.out.println("Url from Twitch " + url.toString());
//
        url = new TwitchFixer().patch(new URL("https://www.twitch.tv/lilousurtwitch/clip/UnsightlyGorgeousTruffleBrainSlug-pkyHV-eBlMh9l0Wz"));
        System.out.println("Url from Twitch " + url.toString());
//
        url = new TwitchFixer().patch(new URL("https://www.twitch.tv/videos/1859049751"));
        System.out.println("Url from Twitch (vod)" + url.toString());


//
//        url = WaterMediaAPI.urlPatch("https://drive.google.com/file/d/1dW33Z_btpX1mR4vbSM2KFJbwNYfPP1Hu/view?usp=drive_link");
//        LOGGER.info(IT, "Url from Google Drive {}", url);


        // These URL may no works

    }
}