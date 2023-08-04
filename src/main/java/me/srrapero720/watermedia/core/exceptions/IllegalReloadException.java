package me.srrapero720.watermedia.core.exceptions;

public class IllegalReloadException extends UnsafeException {
    public IllegalReloadException(String module) {
        super("Rejected attempt to reload " + module, null);
    }
}
