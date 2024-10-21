package org.watermedia.core.exceptions;

import org.watermedia.WaterMedia;

import static org.watermedia.WaterMedia.IT;
import static org.watermedia.WaterMedia.LOGGER;

public class IllegalTLauncherException extends Exception {
    public IllegalTLauncherException() {
        super("TLauncher is NOT supported by " + WaterMedia.NAME + ", please stop using it (and consider safe alternatives like SKLauncher, PrismMC or MultiMC)");
        LOGGER.fatal(IT, "##############################  ILLEGAL LAUNCHER DETECTED  ######################################");
        LOGGER.fatal(IT, "{} refuses to load sensitive modules in a INFECTED launcher, please stop using TLauncher", WaterMedia.NAME);
        LOGGER.fatal(IT, "TLauncher sometimes infects sensitive files after a long time, doing malware stuff that might also infect mods");
        LOGGER.fatal(IT, "Consider use safe alternative like SKLauncher, PrismMC, CurseForge Launcher or the Official launcher");
        LOGGER.fatal(IT, "And please avoid Feather Launcher, TLegacy or any WEIRDSUS-CRACKED LAUNCHER that can me your PC a bitcoin miner");
        LOGGER.fatal(IT, "##############################  ILLEGAL LAUNCHER DETECTED  ######################################");
    }
}