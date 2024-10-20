package me.srrapero720.watermedia.api.config.support;

import me.srrapero720.watermedia.WaterMedia;
import org.watermedia.videolan4j.discovery.DiscoveryProvider;

public class CustomDirectoryProvider implements DiscoveryProvider {
    @Override
    public Priority priority() {
        return Priority.OVERWRITE;
    }

    @Override
    public boolean supported() {
        return true;
    }

    @Override
    public String[] directories() {
        return new String[] {WaterMedia.getLoader().tempDir().resolve("videolan").toAbsolutePath().toString()};
    }
}
