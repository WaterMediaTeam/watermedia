package me.srrapero720.watermedia.api.loader;

public interface IEnvLoader {
    boolean client();
    boolean development();
    boolean installed(String modId);
    boolean tlauncher();
}