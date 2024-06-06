package me.srrapero720.watermedia.core.exceptions;

public class IncompatibleModException extends Exception {
    public IncompatibleModException(String modId, String modName) {
        super(modName + "(" + modId + ")" + " is NOT compatible with WaterMedia. Please remove it to stop crashing");
    }
}