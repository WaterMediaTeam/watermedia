package me.srrapero720.watermedia.vlc.hooks;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.VLCLoader;
import org.slf4j.Logger;

// TODO: added as working hook
public class VLCShutdown extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    public VLCShutdown() {
        super();
        this.setDaemon(true);
        this.setName("VLC-ShutdownHook");
    }

    @Override
    public void run() {
        LOGGER.info("Shutdown VLC");
        if (VLCLoader.getLightState().equals(VLCLoader.Semaphore.READY)) VLCLoader.factory.release();
        LOGGER.info("Shutdown finished");
    }
}