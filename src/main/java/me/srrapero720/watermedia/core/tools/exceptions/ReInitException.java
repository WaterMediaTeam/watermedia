package me.srrapero720.watermedia.core.tools.exceptions;

public class ReInitException extends Exception {
    public ReInitException(String module) {
        super("Rejected attempt to reinit " + module, null);
    }
}