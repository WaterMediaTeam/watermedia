package me.srrapero720.watermedia.api.config.support;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.config.WaterConfig;
import uk.co.caprica.vlcj.discovery.ProviderPriority;
import uk.co.caprica.vlcj.discovery.providers.DiscoveryPathProvider;

public class CustomDirectoryProvider extends DiscoveryPathProvider {
    @Override
    public ProviderPriority priority() {
        return ProviderPriority.OVERWRITE;
    }

    @Override
    public boolean supported() {
        return true;
    }

    @Override
    public String[] directories() {
        return new String[] {
                WaterMedia.getLoader().tmp().resolve("videolan").toAbsolutePath().toString(),
                WaterConfig.vlcInstallPath
        };
    }
}