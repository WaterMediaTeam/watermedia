package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.loader.IModuleBootstrap;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class CacheModule extends IModuleBootstrap {
    private static final Marker IT = MarkerManager.getMarker(CacheCore.class.getSimpleName());
    private static final Map<String, CacheCore.Entry> ENTRIES = new HashMap<>();

    private final File dir;
    private final File index;
    public CacheModule(IMediaLoader loader) {
        super(loader);
        this.dir = loader.tmpPath().toAbsolutePath().resolve("cache/pictures").toFile();
        this.index = new File(dir, "index");
    }

    @Override
    public boolean boot() {
        // LOGGER
        LOGGER.info(IT, "Booting module");
        LOGGER.info(IT, "Mounted on path '{}'", dir);

        if (!dir.exists()) dir.mkdirs();
        if (index.exists()) {
            try (DataInputStream stream = new DataInputStream(new GZIPInputStream(Files.newInputStream(index.toPath())))) {
                int length = stream.readInt();

                for (int i = 0; i < length; i++) {
                    String url = stream.readUTF();
                    String tag = stream.readUTF();
                    long time = stream.readLong();
                    long expireTime = stream.readLong();
                    CacheCore.Entry entry = new CacheCore.Entry(url, !tag.isEmpty() ? tag : null, time, expireTime);
                    ENTRIES.put(entry.getUrl(), entry);
                }
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to load indexes", e);
            }
        }
        return false;
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void release() {
        LOGGER.warn(IT, "Releasing entries");
    }
}