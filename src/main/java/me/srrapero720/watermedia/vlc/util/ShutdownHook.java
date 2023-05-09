package me.srrapero720.watermedia.vlc.util;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.VLCDiscovery;
import org.slf4j.Logger;

public class ShutdownHook extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void run() {
        LOGGER.info("Shutdown VLC");
        if (VLCDiscovery.isAvailable()) VLCDiscovery.factory.release();
        LOGGER.info("Shutdown finished");
    }
}