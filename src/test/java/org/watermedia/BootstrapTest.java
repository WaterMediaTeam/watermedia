package org.watermedia;

import me.srrapero720.watermedia.loader.ILoader;
import org.junit.jupiter.api.BeforeAll;


public class BootstrapTest {

    @BeforeAll
    public static void testBootstrap() throws Exception {
        WaterMedia instance = WaterMedia.prepare(ILoader.DEFAULT);
        instance.start();
    }
}
