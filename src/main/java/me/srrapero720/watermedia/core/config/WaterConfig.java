package me.srrapero720.watermedia.core.config;


import org.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.config.values.ConfigField;
import me.srrapero720.watermedia.core.config.values.WaterConfigFile;

@WaterConfigFile
public class WaterConfig {

    enum CacheMode {
        ENABLE, NO_LOCAL_CACHE, DISABLE
    }

    @ConfigField
    public static String loadingGifFileName = "loading.gif";

    @ConfigField
    public static String vlcInstallPath = WaterMedia.getLoader().tmp().toAbsolutePath().toString();

    @ConfigField
    public static CacheMode cacheMode = CacheMode.ENABLE;

    @ConfigField
    public static String additionalVlcPlayerArgs = "";

    @ConfigField
    public static String additionalVlcFactoryArgs = "";
}