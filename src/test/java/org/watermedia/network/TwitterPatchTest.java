package org.watermedia.network;

import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.patchs.TwitterPatch;
import org.junit.jupiter.api.Test;
import org.watermedia.BootstrapTest;

import static org.junit.jupiter.api.Assertions.*;
import static me.srrapero720.watermedia.WaterMedia.*;

public class TwitterPatchTest {
    private static final String EXAMPLE_URI = "https://x.com/SrRap720/status/1842440861964984539";

    @Test
    public void testValidatorData() {
        MediaURI source = MediaURI.get(BootstrapTest.CONTEXT, EXAMPLE_URI);
        TwitterPatch patch = new TwitterPatch();

        assertTrue(patch.validate(source));
        try {
            patch.patch(source, BootstrapTest.CONTEXT);
        } catch (Exception e) {
            throw new RuntimeException("Failed to patch URL", e);
        }

        assertTrue(source.patched());
        var sources = source.getSources();
        assertEquals(4, sources.length);

        LOGGER.debug(sources.length);

        for (MediaURI.Source s: sources) {
            LOGGER.debug(s.toString());
        }
    }
}
