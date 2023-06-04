package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.vlc.VLCState;
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
        if (VLCLoader.getLightState().equals(VLCState.READY)) VLCLoader.getFactory().release();
        LOGGER.info("Shutdown finished");
    }
}