package me.srrapero720.watermedia.tools.exceptions;

import static me.srrapero720.watermedia.WaterMedia.IT;
import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class IllegalEnvironmentException extends Exception {
    public IllegalEnvironmentException() {
        super("Cannot run WaterMedia on SERVER-SIDE. Remove mod from server and keep it on client");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
        LOGGER.error(IT, "WM is not designed for server-side, please remove it from server and keep it on client");
        LOGGER.error(IT, "If other mod requires WaterMedia on server-side, report it immediately with the creator");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
    }
}