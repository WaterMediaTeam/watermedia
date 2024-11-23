package org.watermedia.network;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;
import org.watermedia.core.network.patchs.TwitterPatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.watermedia.WaterMedia.*;

public class TwitterPatchTest {
    public static final MediaContext CONTEXT = new MediaContext.Simple("twitter_test", "Twitter Patch test");
    private static final String EXAMPLE_URI = "https://x.com/SrRap720/status/1842440861964984539";

    @Test
    public void testTwitterConnection() {
        MRL source = MRL.get(CONTEXT, EXAMPLE_URI);
        TwitterPatch patch = new TwitterPatch();

        assertTrue(patch.validate(source));
        try {
            patch.patch(source, CONTEXT);
        } catch (Exception e) {
            throw new RuntimeException("Failed to patch URL", e);
        }

        assertTrue(source.patched());
        var sources = source.getSources();
        assertEquals(4, sources.length);

        for (MRL.Source s: sources) {
            LOGGER.debug(s.toString());
        }
    }
}
