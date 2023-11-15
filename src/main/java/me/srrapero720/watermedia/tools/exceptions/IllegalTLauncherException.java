package me.srrapero720.watermedia.tools.exceptions;

import static me.srrapero720.watermedia.WaterMedia.IT;
import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class IllegalTLauncherException extends Exception {
    public IllegalTLauncherException() {
        super("TLauncher is NOT supported by WATERMeDIA, please stop using it (and consider safe alternatives like SKLauncher or MultiMC)");
        LOGGER.error(IT, "#################################  ILLEGAL LAUNCHER  #########################################");
        LOGGER.error(IT, "WATERMeDIA refuses to load sensitive modules in a INFECTED launcher, please stop using TLauncher");
        LOGGER.error(IT, "Consider use another safe alternative like SKLauncher, MultiMC or Buy the game and use original one");
        LOGGER.error(IT, "#################################  ILLEGAL LAUNCHER  #########################################");
    }
}