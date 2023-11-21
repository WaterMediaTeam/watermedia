package me.srrapero720.watermedia.tests;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.loaders.IBootCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FixersTest {
    private static final Marker IT = MarkerManager.getMarker("Fixers");
    public static void main(String[] args) {
        Test.LOGGER.info(IT, "Running Bootstrap test");
        try {
            Test.WATERMEDIA = WaterMedia.create(IBootCore.DEFAULT);
            Test.WATERMEDIA.init();

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
        } catch (Exception e) {
            Test.LOGGER.warn(IT, "Test failed", e);
            System.exit(-1);
        }
    }
}