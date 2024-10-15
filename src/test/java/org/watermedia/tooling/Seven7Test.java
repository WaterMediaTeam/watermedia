package org.watermedia.tooling;

import org.watermedia.tools.IOTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.junit.jupiter.api.Test;

import java.io.File;

public class Seven7Test {
    private static final Marker IT = MarkerManager.getMarker(Seven7Test.class.getSimpleName());

    @Test
    public void testVideoLanExtraction() throws Exception {
        File zip = new File("run/win-x64.7z");
        IOTool.un7zip(zip.toPath());

        // delete test shit
        for (File file: zip.getParentFile().listFiles()) {
            if (!file.isDirectory()) {
                if (!file.equals(zip)) file.delete();
            } else {
                deleteFolder(file);
            }
        }
    }

    private static void deleteFolder(File file) {
        for (File f: file.listFiles()) {
            if (f.isDirectory()) deleteFolder(f);
            else f.delete();
        }
        file.delete();
    }
}
