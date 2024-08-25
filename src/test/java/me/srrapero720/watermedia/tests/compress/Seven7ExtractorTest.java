package me.srrapero720.watermedia.tests.compress;

import me.srrapero720.watermedia.tools.IOTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.junit.jupiter.api.Test;

import java.io.File;

public class Seven7ExtractorTest {
    private static final Marker IT = MarkerManager.getMarker(Seven7ExtractorTest.class.getSimpleName());

    @Test
    public void testSeven7() throws Exception {
        IOTool.un7zip(IT, new File("run/win-x64.7z").toPath());
    }
}
