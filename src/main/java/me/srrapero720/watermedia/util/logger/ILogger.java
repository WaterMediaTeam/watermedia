package me.srrapero720.watermedia.util.logger;

public interface ILogger {
    void info(IMarker marker, String message, Throwable t);
    void error(IMarker marker, String message, Throwable t);
    void err(IMarker marker, String message, Throwable t);
    void debug(IMarker marker, String message, Throwable t);

}
