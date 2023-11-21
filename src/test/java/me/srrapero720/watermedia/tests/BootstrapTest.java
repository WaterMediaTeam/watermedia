package me.srrapero720.watermedia.tests;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.IBootCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class BootstrapTest {
    private static final Marker IT = MarkerManager.getMarker("Bootstrap");
    public static void main(String[] args) {
        Test.LOGGER.info(IT, "Running Bootstrap test");
        try {
            Test.WATERMEDIA = WaterMedia.create(IBootCore.DEFAULT);
            Test.WATERMEDIA.init();

            System.exit(0);
        } catch (Exception e) {
            Test.LOGGER.warn(IT, "Test failed", e);
            System.exit(-1);
        }
    }
}