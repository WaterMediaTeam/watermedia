package me.srrapero720.watermedia;

public abstract class WMConfig {

    public abstract boolean getLowQualityTrend();
    public abstract MediaQuality getQuality();

    public enum MediaQuality { LOW, MID, HIGH }
}
