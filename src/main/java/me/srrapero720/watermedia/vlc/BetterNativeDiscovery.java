package me.srrapero720.watermedia.vlc;

import com.mojang.logging.LogUtils;
import com.sun.jna.NativeLibrary;
import me.srrapero720.watermedia.tools.ReflectTools;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class BetterNativeDiscovery extends NativeDiscovery {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Field searchPaths;
    private static Field libraries;

    public BetterNativeDiscovery(NativeDiscoveryStrategy... discoveryStrategies) {
        super(discoveryStrategies);
    }

    @SuppressWarnings("unchecked")
    public boolean attemptFix(String path, NativeDiscoveryStrategy discoveryStrategy) {
        if (searchPaths == null) {
            searchPaths = ReflectTools.field(NativeLibrary.class, "searchPaths");
            libraries = ReflectTools.field(NativeLibrary.class, "libraries");
        }
        try {
            Map<String, Reference<NativeLibrary>> libs = (Map<String, Reference<NativeLibrary>>) libraries.get(null);
            Map<String, List<String>> paths = (Map<String, List<String>>) searchPaths.get(null);
            libs.remove(RuntimeUtil.getLibVlcCoreLibraryName());
            paths.remove(RuntimeUtil.getLibVlcCoreLibraryName());
            libs.remove(RuntimeUtil.getLibVlcLibraryName());
            paths.remove(RuntimeUtil.getLibVlcLibraryName());
            LOGGER.info("Failed to load VLC in '{}'", path);
            return true;
        } catch (IllegalArgumentException | IllegalAccessException ignored) {}
        return false;
    }

    @Override
    protected void onFailed(String path, NativeDiscoveryStrategy strategy) {
        LOGGER.info("Failed to load VLC in '{}' stop searching", path);
        super.onFailed(path, strategy);
    }

    @Override
    protected void onNotFound() {
        LOGGER.info("Could not find VLC in any of the given paths");
        super.onNotFound();
    }
}
