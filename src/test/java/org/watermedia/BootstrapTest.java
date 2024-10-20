package org.watermedia;

import org.junit.jupiter.api.BeforeAll;


public class BootstrapTest {

    @BeforeAll
    public static void testBootstrap() throws Exception {
        WaterMedia instance = WaterMedia.prepare(WaterMedia.DEFAULT_LOADER);
        instance.start();
    }
}
