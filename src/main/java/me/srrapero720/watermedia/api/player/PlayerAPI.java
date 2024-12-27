package me.srrapero720.watermedia.api.player;

import org.watermedia.WaterMedia;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.IOTool;
import org.watermedia.tools.JarTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());
    public static final Map<String, MediaPlayerFactory> FACTORIES = new LinkedHashMap<>();

    private static MediaPlayerFactory DEFAULT_FACTORY;
    private static MediaPlayerFactory DEFAULT_SONG_FACTORY;


    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() {
        return NativeDiscovery.isDiscovered();
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
     * @return MediaPlayerFactory to create custom VLC players.
     */
    public static MediaPlayerFactory registerFactory(String resLoc, String[] vlcArgs) {
        if (NativeDiscovery.discovery()) {
            MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
            MediaPlayerFactory oldFactory = FACTORIES.put(resLoc, factory);
            LOGGER.info(IT, "Created new VLC instance from '{}' with args: '{}'", NativeDiscovery.getDiscoveredPath(), Arrays.toString(vlcArgs));
            if (oldFactory != null) LOGGER.warn(IT, "Factory {} previously defined was overwritted", resLoc);
            return factory;
        }

        LOGGER.fatal(IT, "[VLC IS MISSING]: Cannot create MediaPlayerFactory instance");
        return null;
    }

    // LOADING
    private Path dir;
    private Path logs;

    private String zipInput;
    private String configInput;

    private File zipOutput;
    private File configOutput;

    private boolean wrapped;
    private boolean extract;

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        LOGGER.info(IT, "Binaries are {}", wrapped ? "wrapped" : "not wrapped");
        if (wrapped) {
            String versionInJar = JarTool.readString(configInput);
            String versionInFile = IOTool.readString(configOutput.toPath());

            boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);

            File[] f = zipOutput.getParentFile().listFiles();
            boolean hasExtracted = f != null && f.length > 4; // libvlc.dll, libvlccore.dll, plugins and version.cfg

            if (!versionMatch) {
                this.extract = true;
                LOGGER.info(IT, "Binaries not extracted, extraction scheduled");
                if (zipOutput.getParentFile().exists()) {
                    LOGGER.warn(IT, "Detected an old installation, cleaning up...");
                    try {
                        IOTool.rmdirs(zipOutput.getParentFile());
                    } catch (Exception e) {
                        LOGGER.error(IT, "Failed to delete directories", e);
                    }
                    LOGGER.warn(IT, "Cleaning up successfully");
                }
            } else {
                if (!hasExtracted) {
                    this.extract = true;
                    LOGGER.warn(IT, "Binaries extraction scheduled due to a broken or missing installation");
                } else {
                    LOGGER.warn(IT, "VLC binaries extraction skipped. Extracted version match with wrapped version");
                }
            }
        } else {
            LOGGER.warn(IT, "[NOT A BUG] {} doesn't contains VLC binaries for your OS and ARCH, you had to download it manually from 'https://www.videolan.org/vlc/'", WaterMedia.NAME);
        }

        return true;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception {
        if (extract) {
            LOGGER.info(IT, "Extracting VideoLAN binaries...");
            if ((!zipOutput.exists() && JarTool.extract(zipInput, zipOutput.toPath())) || zipOutput.exists()) {
                IOTool.un7zip(zipOutput.toPath());
                if (!zipOutput.delete()) {
                    LOGGER.error(IT, "Failed to delete binaries zip file...");
                }

                JarTool.extract(configInput, configOutput.toPath());

                LOGGER.info(IT, "VideoLAN binaries extracted successfully");
            } else {
                LOGGER.error(IT, "Failed to extract VideoLAN binaries");
            }
        }

        try {
            String[] args = JarTool.readArray("videolan/arguments.json");
            registerFactory(WaterMedia.asResource("default"), args);
            registerFactory(WaterMedia.asResource("sound_only"), DataTool.concat(args, "--vout=none"));

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