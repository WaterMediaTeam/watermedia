package org.watermedia;

import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.loaders.ILoader;

import java.io.File;
import java.net.URI;

public class FixerTester {
    public static void main(String[] args) throws Exception {
        WaterMedia.prepare(ILoader.DEFAULT).start();

        URI uri = new File("c:\\user\\jrap\\dempath").toURI();
        URI stringedUri = URI.create(uri.toString());

        WaterMedia.LOGGER.info("URI IS {}", uri);
        WaterMedia.LOGGER.info("STRINGIED URI IS {}", stringedUri);


        AbstractPatch.Result result = NetworkAPI.patch("https://kick.com/brunenger");
        WaterMedia.LOGGER.info("Result is: {}", result);

        System.exit(0);
    }
}