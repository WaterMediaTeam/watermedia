package me.srrapero720.watermedia.vlc.util;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.VLC;
import org.slf4j.Logger;

// TODO: added as working hook
public class VLCShutdownHook extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();

    public VLCShutdownHook() {
        super();
        this.setDaemon(true);
        this.setName("VLC-ShutdownHook");
    }

    @Override
    public void run() {
        LOGGER.info("Shutdown VLC");
        if (VLC.getLightState().equals(VLC.Semaphore.READY)) VLC.factory.release();
        LOGGER.info("Shutdown finished");
    }
}