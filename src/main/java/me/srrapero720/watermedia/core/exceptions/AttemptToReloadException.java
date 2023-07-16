package me.srrapero720.watermedia.core.exceptions;

public class AttemptToReloadException extends SafeException {
    public AttemptToReloadException(String module) {
        super("Rejected attempt to reload " + module);
    }
}
