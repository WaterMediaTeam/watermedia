package me.srrapero720.watermedia.vlc.hooks;

import me.srrapero720.watermedia.vlc.VLCLoader;
import static me.srrapero720.watermedia.WaterMedia.LOGGER;

// TODO: added as working hook
public class VLCShutdown extends Thread {
    public VLCShutdown() {
        super();
        this.setDaemon(true);
        this.setName("VLC-ShutdownHook");
    }

    @Override
    public void run() {
        LOGGER.info("Shutdown VLC");
        LOGGER.error("Shutdown is disabled... system should close itself");
//        if (VLCLoader.getLightState().equals(VLCLoader.Semaphore.READY)) VLCLoader.factory.release();
        LOGGER.info("Shutdown finished");
    }
}