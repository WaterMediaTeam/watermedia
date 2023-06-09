package me.srrapero720.watermedia;

public abstract class MediaConfig {

    public abstract boolean getLowQualityTrend();
    public abstract MediaQuality getQuality();

    public enum MediaQuality { LOW, MID, HIGH }
}
