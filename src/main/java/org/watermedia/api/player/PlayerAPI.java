package org.watermedia.api.player;

import com.sun.jna.Platform;
import org.watermedia.WaterMedia;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.api.player.videolan.BasePlayer;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.IOTool;
import org.watermedia.core.tools.JarTool;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.videolan4j.discovery.providers.IProvider;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;
import org.watermedia.videolan4j.discovery.NativeDiscovery;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());
    public static final Map<String, MediaPlayerFactory> FACTORIES = new LinkedHashMap<>();

    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() {
        return NativeDiscovery.discovered();
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
     * @param id the identifier (ResourceLocation#toString())
     * @param vlcArgs arguments used to create new VLC player instances
     * @return MediaPlayerFactory to create custom VLC players. {@link BasePlayer} can accept factory for new instances
     */
    public static synchronized MediaPlayerFactory registerFactory(String id, String[] vlcArgs) {
        if (NativeDiscovery.start()) {
            MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
            MediaPlayerFactory oldFactory = FACTORIES.put(id, factory);
            LOGGER.info(IT, "Created new VLC instance with ID '{}' with args: '{}'", id, Arrays.toString(vlcArgs));
            if (oldFactory != null) {
                LOGGER.warn(IT, "Factory {} previously defined was overwritten", id);
                oldFactory.release();
            }
            return factory;
        }

        LOGGER.fatal(IT, "[VLC IS MISSING]: Cannot create MediaPlayerFactory instance");
        return null;
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
        String zFilename = "win-x64.zip";

        this.dir = bootstrap.tempDir().resolve("videolan");

        this.zipInput = "videolan/" + zFilename;
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
        if (WaterMedia.NO_VLC.getAsBoolean()) {
            LOGGER.warn(IT, "Detected {}, skipping PlayerAPI loading", WaterMedia.NO_VLC);
            return false;
        }

        if (wrapped) {
            String versionInJar = JarTool.readString(configInput);
            String versionInFile = IOTool.readString(configOutput);

            boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);

            File[] f = zipOutput.getParentFile().listFiles();
            boolean hasExtracted = f != null && f.length > 4; // libvlc.dll, libvlccore.dll, plugins and version.cfg

            if (!versionMatch) {
                this.extract = true;
                LOGGER.info(IT, "Binaries extraction scheduled");
                if (zipOutput.getParentFile().exists()) {
                    LOGGER.warn(IT, "Old installation detected, cleaning it...");

                    if (IOTool.rmdirs(zipOutput.getParentFile()))
                        LOGGER.info(IT, "Cleanup successfully");
                    else
                        LOGGER.error("Failed to delete old installation");
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
    public void start(ILoader bootCore) throws Exception {
        if (extract) {
            LOGGER.info(IT, "Extracting VideoLAN binaries...");
            if ((!zipOutput.exists() && JarTool.copyAsset(zipInput, zipOutput.toPath())) || zipOutput.exists()) {
                IOTool.unzip(zipOutput.toPath());
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
            String[] args = JarTool.readArray(Platform.isWindows() ? "videolan/arguments.json" : "videolan/arguments_linux.json");
            registerFactory(WaterMedia.asResource("default"), args);
            registerFactory(WaterMedia.asResource("sound_only"), DataTool.concat(args, "--vout=none"));
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    @Override
    public void release() {
        FACTORIES.forEach((s, mediaPlayerFactory) -> mediaPlayerFactory.release());
        FACTORIES.clear();
    }

    public static class ConfigProvider implements IProvider {
        private static final File customPathFile = WaterMedia.getConfigDir().resolve("custom_vlc_path.txt").toFile();
        private String content;

        public ConfigProvider() {
            if(!customPathFile.exists())
                IOTool.writeData(customPathFile, new byte[0]);
        }

        @Override
        public String name() {
            return "WaterMedia Config Provider";
        }

        @Override
        public Priority priority() {
            return Priority.OVERWRITE;
        }

        @Override
        public boolean supported() {
            // first we check
            if (content == null)
                content = IOTool.readString(customPathFile);

            // then we reinforce
            if (content == null || content.isEmpty())
                return false;

            // now we validate
            File f = new File(content);
            return f.exists() && f.isDirectory();
        }

        @Override
        public String[] directories() {
            return new String[] {
                    content
            };
        }
    }

    public static class Provider implements IProvider {
        @Override
        public String name() {
            return "WaterMedia Provider";
        }

        @Override
        public Priority priority() {
            return Priority.HIGHEST;
        }

        @Override
        public boolean supported() {
            return Platform.isWindows() && Platform.is64Bit();
        }

        @Override
        public String[] directories() {
            return new String[] {WaterMedia.getLoader().tempDir().resolve("videolan").toAbsolutePath().toString()};
        }
    }
}