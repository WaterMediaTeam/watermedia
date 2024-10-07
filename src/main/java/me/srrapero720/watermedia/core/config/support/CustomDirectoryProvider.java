package me.srrapero720.watermedia.core.config.support;

import org.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.config.WaterConfig;
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