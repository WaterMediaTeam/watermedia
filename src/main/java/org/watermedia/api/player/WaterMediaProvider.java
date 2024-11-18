package org.watermedia.api.player;

import com.sun.jna.Platform;
import org.watermedia.WaterMedia;
import org.watermedia.videolan4j.discovery.providers.IProvider;

public class WaterMediaProvider implements IProvider {
    @Override
    public Priority priority() {
        return Priority.OVERWRITE;
    }

    @Override
    public boolean supported() {
        return Platform.isWindows() && Platform.is64Bit();
    }

    @Override
    public String[] directories() {
        return new String[] {WaterMedia.getLoader().tempDir().resolve("videolan").toAbsolutePath().toString()};
    }
}
