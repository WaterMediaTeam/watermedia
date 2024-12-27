package org.watermedia.core.exceptions;

public class UnsupportedArchitechtureException extends UnsupportedOperationException {
    public UnsupportedArchitechtureException() {
        super("32 bits is not supported");
    }
}
