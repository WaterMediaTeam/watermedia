package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;

public class FixerTester {
    public static void main(String[] args) {
        URLFixer.Result result = UrlAPI.fixURL("https://imgur.com/t/rick_roll/kGy6J8J");
        WaterMedia.LOGGER.info("Result is: {}", result);

        result = UrlAPI.fixURL("D:\\Videos\\Grabaciones\\2023-10-16 00-19-11.mp4");
        WaterMedia.LOGGER.info("Result is: {}", result);

        result = UrlAPI.fixURL("https://imgur.com/4JMVauZ");
        WaterMedia.LOGGER.info("Result is: {}", result);

        result = UrlAPI.fixURL("https://imgur.com/a/TIiiR21");
        WaterMedia.LOGGER.info("Result is: {}", result);

        result = UrlAPI.fixURL("https://imgur.com/t/art/gm4Q7kN");
        WaterMedia.LOGGER.info("Result is: {}", result);

        System.exit(0);
    }
}