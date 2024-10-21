package org.watermedia;

import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.loaders.ILoader;

public class FixerTester {
    public static void main(String[] args) throws Exception {
        WaterMedia.prepare(ILoader.DEFAULT).start();

        AbstractPatch.Result result = NetworkAPI.patch("https://www.twitch.tv/videos/2278364046");
        WaterMedia.LOGGER.info("Result is: {}", result);

        System.exit(0);
    }
}