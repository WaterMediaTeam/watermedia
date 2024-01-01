package me.srrapero720.watermedia.tests.boxing;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;

import java.util.Arrays;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class RawMacOSTester {
    private static final Marker IT = MarkerManager.getMarker("MacOsTester");
    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    private static final String[] ARGS = new String[] {
            "--no-quiet",

            "--file-logging",
            "--logmode=text",
            "--logfile={logfile}",
            "--verbose=2",

            "--aout=directsound",

            "--http-reconnect"
    };

    public static void main(String[] args) {
        new RawMacOSTester().start("");
    }

    private final CallbackMediaPlayerComponent playerComponent;
    public RawMacOSTester() {
        MediaPlayerFactory factory = null;
        if (DISCOVERY.discover()) {
            factory = new MediaPlayerFactory(DISCOVERY, ARGS);
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            LOGGER.info(IT, "Created new VLC instance from '{}' with args: '{}'", DISCOVERY.discoveredPath(), Arrays.toString(ARGS));
        } else {
            LOGGER.fatal(IT, "Missing VLC - Cannot create MediaPlayerFactory instance");
        }

        playerComponent = new CallbackMediaPlayerComponent(factory, false,
                (mediaPlayer, nativeBuffers, bufferFormat) -> {},
                (sourceWidth, sourceHeight) -> new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[]{sourceWidth * 4}, new int[]{sourceHeight}));

        playerComponent.mediaPlayer().audio().setVolume(80); // here is where it crashes
    }

    public void start(String url) {
        playerComponent.mediaPlayer().media().start(url);
    }
}
