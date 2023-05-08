package me.srrapero720.watermedia.vlc;

import com.mojang.logging.LogUtils;
import com.sun.jna.NativeLibrary;
import me.srrapero720.watermedia.tools.ReflectTools;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Overwrite;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;


import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class BetterNativeDiscovery extends NativeDiscovery {
    private static final Logger LOG = LogUtils.getLogger();
    private static Field searchPaths;
    private static Field libraries;

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
            LOG.info("Failed to load VLC in '{}'", path);
            return true;
        } catch (IllegalArgumentException | IllegalAccessException e) {}
        return false;
    }
}
