package me.srrapero720.watermedia.api.loader;

public interface IEnvLoader {
    boolean tlauncher();
    boolean development();
    boolean client();
    boolean installed(String modId);
}
