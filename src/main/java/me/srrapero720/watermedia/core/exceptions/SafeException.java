package me.srrapero720.watermedia.core.exceptions;

public class SafeException extends Exception {
    public SafeException(String msg, Exception cause) {
        super(msg, cause);
    }

    public SafeException(String msg) {
        super(msg);
    }
}
