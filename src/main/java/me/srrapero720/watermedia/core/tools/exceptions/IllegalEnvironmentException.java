package me.srrapero720.watermedia.core.tools.exceptions;

import me.srrapero720.watermedia.WaterMedia;

import static me.srrapero720.watermedia.WaterMedia.IT;
import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class IllegalEnvironmentException extends Exception {
    public IllegalEnvironmentException() {
        super(WaterMedia.NAME + "CANNOT be used or installed on SERVER-SIDE. Just remove " + WaterMedia.NAME + " for server, and keep it on client");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
        LOGGER.error(IT, "MOD is not designed for server-side, please remove it from server and keep it on client");
        LOGGER.error(IT, "If other mod requires WaterMedia on server-side, report it immediately with the creator");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
    }
}