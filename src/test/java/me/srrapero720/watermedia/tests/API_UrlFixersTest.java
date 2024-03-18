package me.srrapero720.watermedia.tests;

import me.srrapero720.watermedia.tools.JarTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class API_UrlFixersTest extends Test {
    private static final Marker IT = MarkerManager.getMarker("Fixers");

    Map<String, ArrayList<String>> SOURCES;

    @Override
    protected void prepare() throws Exception {
        SOURCES = JarTool.readObject("fixers-test.json");

//        UrlAPI boot = new UrlAPI();
//        boot.prepare(ILoader.DEFAULT);
//        boot.start(ILoader.DEFAULT);
        LOGGER.info(IT, "API started");
    }

    @Override
    protected void run() {
        Set<String> keys = SOURCES.keySet();

        for (String key: keys) {
            LOGGER.info(IT, "Running test for {}", key.replace(":soft", ""));
            for (String value: SOURCES.get(key)) {
                try {
//                    URLFixer.Result result = UrlAPI.fixURL(value);
//                    if (result == null) throw new IllegalStateException("Fixer failed with '" + value + "'");

//                    LOGGER.debug(IT, "Fix successfully {}", result.toString());
                    // TODO: fetch URL to check if status code was 200
                } catch (Exception e) {
                    if (key.contains(":soft")) {
                        LOGGER.error(IT, "Failed to fix url '{}'", value, e);
                        LOGGER.error(IT, "Skipping because might be not currently available");
                    } else {
                        throw new RuntimeException("Failed fixing urls", e);
                    }
                }
            }
        }
    }

    @Override
    protected void release() throws Exception {

    }
}