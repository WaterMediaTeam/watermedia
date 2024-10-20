package me.srrapero720.watermedia.api.player;

import com.sun.jna.Platform;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.vlc.SimplePlayer;
import me.srrapero720.watermedia.core.tools.IOTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());

    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    public static final Map<String, MediaPlayerFactory> FACTORIES = new LinkedHashMap<>();

    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() {
        return NativeDiscovery.alreadyFound;
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance
     * uses DirectSound by default, witch provides an individual volume for each player
     * by default uses on video output "mem"
     * @return default factory
     */
    public static MediaPlayerFactory getFactory() {
        return FACTORIES.get(WaterMedia.asResource("default"));
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance
     * uses DirectSound by default, witch provides an individual volume for each player
     * by default uses on video output "mem"
     * @return default factory
     */
    public static MediaPlayerFactory getFactory(String resourceLocation) {
        return FACTORIES.get(resourceLocation);
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance
     * uses DirectSound and No video output by default, witch provides an individual volume for each player
     * by default uses on video output "none"
     * @return default factory
     */
    public static MediaPlayerFactory getFactorySoundOnly() {
        return FACTORIES.get(WaterMedia.asResource("sound_only"));
    }

    /**
     * Registers a new FACTORY associated with a identifier (or a well-know Minecraft ResourceLocation as String)
     * check <a href="https://wiki.videolan.org/VLC_command-line_help/">VideoLAN wiki</a>
     * @param resLoc the identifier (ResourceLocation#toString())
     * @param vlcArgs arguments used to create new VLC player instances
     * @return MediaPlayerFactory to create custom VLC players. {@link SimplePlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory registerFactory(String resLoc, String[] vlcArgs) {
        if (DISCOVERY.discover()) {
            MediaPlayerFactory factory = new MediaPlayerFactory(DISCOVERY, vlcArgs);
            MediaPlayerFactory oldFactory = FACTORIES.put(resLoc, factory);
            LOGGER.info(IT, "Created new VLC instance from '{}' with args: '{}'", DISCOVERY.discoveredPath(), Arrays.toString(vlcArgs));
            if (oldFactory != null) LOGGER.warn(IT, "Factory {} previously defined was overwritted", resLoc);
            return factory;
        }

        LOGGER.fatal(IT, "[VLC IS MISSING]: Cannot create MediaPlayerFactory instance");
        return null;
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release factory on process shutdown
     * Suggestion: Use the same VLC arguments for logging but with another filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * check <a href="https://wiki.videolan.org/VLC_command-line_help/">VideoLAN wiki</a>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link SimplePlayer} can accept factory for new instances
     * @deprecated use instead {@link PlayerAPI#registerFactory(String, String[])}. Fallback here is not efficient
     */
    public static MediaPlayerFactory customFactory(String[] vlcArgs) {
        int fakeID = 0;
        while (FACTORIES.containsKey(WaterMedia.asResource("unidentified_" + fakeID))) {
            fakeID++;
        }
        return registerFactory(WaterMedia.asResource("unidentified_" + fakeID), vlcArgs);
    }

    // LOADING
    private final Path dir;

    private final String zipInput;
    private final String configInput;

    private final File zipOutput;
    private final File configOutput;

    private boolean extract = false;
    // NOTE: this got hardcoded because V3 will use FFMPEG with all platform binaries
    private final boolean wrapped = Platform.isWindows() && Platform.is64Bit();
    public PlayerAPI() {
        super();
        ILoader bootstrap = WaterMedia.getLoader();
        String zFilename = "win-x64.7z";

        this.dir = bootstrap.tempDir().resolve("videolan");

        this.zipInput = "videolan/" + "win-x64.7z";
        this.configInput = "videolan/version.cfg";

        if (this.wrapped) {
            this.zipOutput = dir.resolve(zFilename).toFile();
            this.configOutput = dir.resolve("version.cfg").toFile();
        } else {
            zipOutput = configOutput = null;
        }
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        LOGGER.info(IT, "Binaries are {}", wrapped ? "wrapped" : "not wrapped");
        if (wrapped) {
            String versionInJar = JarTool.readString(configInput);
            String versionInFile = IOTool.readString(configOutput.toPath());

            boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);
            if (!versionMatch) {
                this.extract = true;
                LOGGER.info(IT, "Binaries not extracted, extraction scheduled");
                if (zipOutput.getParentFile().exists()) {
                    LOGGER.warn(IT, "Detected an old installation, cleaning up...");
                    try {
                        FileUtils.deleteDirectory(zipOutput.getParentFile());
                    } catch (Exception e) {
                        LOGGER.error(IT, "Failed to delete directories", e);
                    }
                    LOGGER.warn(IT, "Cleaning up successfully");
                }
            } else {
                LOGGER.warn(IT, "VLC binaries extraction skipped. Extracted version match with wrapped version");
            }
        } else {
            LOGGER.warn(IT, "[NOT A BUG] {} doesn't contains VLC binaries for your OS and ARCH, you had to download it manually from 'https://www.videolan.org/vlc/'", WaterMedia.NAME);
        }

        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (wrapped) {
            LOGGER.info(IT, "Extracting VideoLAN binaries...");
            if ((!zipOutput.exists() && JarTool.copyAsset(zipInput, zipOutput.toPath())) || zipOutput.exists()) {
                IOTool.un7zip(IT, zipOutput.toPath());
                if (!zipOutput.delete()) {
                    LOGGER.error(IT, "Failed to delete binaries zip file...");
                }

                JarTool.copyAsset(configInput, configOutput.toPath());

                LOGGER.info(IT, "VideoLAN binaries extracted successfully");
            } else {
                LOGGER.error(IT, "Failed to extract VideoLAN binaries");
            }
        }

        try {
            String[] args = JarTool.readArray("videolan/arguments.json");
            registerFactory(WaterMedia.asResource("default"), args);
            registerFactory(WaterMedia.asResource("sound_only"), ArrayUtils.addAll(args, "--vout=none"));

            Runtime.getRuntime().addShutdownHook(new Thread(this::release));
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    @Override
    public void release() {
        FACTORIES.forEach((s, mediaPlayerFactory) -> mediaPlayerFactory.release());
        FACTORIES.clear();
    }
}