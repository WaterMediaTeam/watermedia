package me.srrapero720.watermedia.vlc;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class LocalFileDiscoveryDirectoryProvider implements DiscoveryDirectoryProvider {
    
    @Override
    public int priority() {
        return 5;
    }
    
    @Override
    public boolean supported() {
        return true;
    }
    
    @Override
    public String[] directories() {
        // TODO: remove FML and use GAMEDIR from VLC.class
        Path vlc = FMLPaths.GAMEDIR.get().resolve("cache/vlc/");

        if (RuntimeUtil.isNix()) vlc = vlc.resolve("linux");
        else if (RuntimeUtil.isMac()) vlc = vlc.resolve("mac");
        else {
            boolean is64 = System.getProperty("sun.arch.data.model").equals("64");
//            vlc = vlc.resolve("windows_" + (is64 ? "x64" : "x86"));
        }

        LOGGER.info("Absolute path from {}", vlc.toAbsolutePath().toFile());
        return new String[] { vlc.toAbsolutePath().toString() };
    }
}