package org.watermedia;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.loader.ILoader;
import org.junit.jupiter.api.BeforeAll;


public class BootstrapTest {
    public static final MediaContext CONTEXT = new MediaContext.Simple("test", "Test");

    @BeforeAll
    public static void testBootstrap() throws Exception {
        WaterMedia instance = WaterMedia.prepare(ILoader.DEFAULT);
        instance.start();
    }
}
