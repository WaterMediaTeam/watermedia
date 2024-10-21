package org.watermedia.core.exceptions;

public class UnsupportedArchitechtureException extends UnsupportedOperationException {
    public UnsupportedArchitechtureException() {
        super("32 bits are not supported");
    }
}
