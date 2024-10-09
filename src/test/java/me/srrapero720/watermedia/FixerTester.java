package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.loaders.ILoader;

public class FixerTester {
    public static void main(String[] args) throws Exception {
        WaterMedia.prepare(ILoader.DEFAULT).start();

        URLFixer.Result result = UrlAPI.fixURL("https://kick.com/w0lvez");
        WaterMedia.LOGGER.info("Result is: {}", result);

        System.exit(0);
    }
}