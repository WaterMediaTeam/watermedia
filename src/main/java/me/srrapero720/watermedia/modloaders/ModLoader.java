package me.srrapero720.watermedia.modloaders;

import me.srrapero720.watermedia.tools.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.tools.exceptions.IncompatibleModException;

public class ModLoader {
    public static void envInit(IModLoader loader) throws Exception {
        if (!loader.clientSide() && loader.enforceCrash()) throw new IllegalEnvironmentException();
        if (loader.modPresent("fancyvideo_api")) throw new IncompatibleModException("fancyvideo_api", "FancyVideo-API");
        if (loader.tlcheck()) throw new IllegalStateException("TLauncher is UNSUPPORTED. Use instead SKLauncher or MultiMC");
    }
}