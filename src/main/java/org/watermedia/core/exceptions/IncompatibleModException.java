package org.watermedia.core.exceptions;

public class IncompatibleModException extends Exception {
    public IncompatibleModException(String modId, String modName, String alternative) {
        super(modName + "(" + modId + ")" + " is NOT compatible with WaterMedia. Please replace it with " + alternative);
    }
}