package me.srrapero720.watermedia.core.cache;

import me.srrapero720.watermedia.core.WaterInternalAPI;
import me.srrapero720.watermedia.core.config.WaterConfig;
import me.srrapero720.watermedia.loader.ILoader;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.PairTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.watermedia.WaterMedia.LOGGER;

public class CacheCore extends WaterInternalAPI {
    private static final Marker IT = MarkerManager.getMarker(CacheCore.class.getSimpleName());
    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private static File dir;
    private static File index;

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        dir = new File(WaterConfig.vlcInstallPath, "cache/pictures");
        index = new File(dir, "index");
        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (!dir.exists() && !dir.mkdirs())
            throw new IOException("Failed to create cache directories");

        if (index.exists()) {
            try (DataInputStream in = new DataInputStream(new GZIPInputStream(Files.newInputStream(index.toPath())))) {
                while (in.available() != 0) {
                    Entry entry = Entry.read(in);
                    ENTRIES.put(entry.url, entry);
                }
            }
        }
    }

    @Override
    public void release() {
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(index.toPath())))) {
            for (Entry entry: ENTRIES.values()) {
                entry.write(out);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to save cache indexes", e);
        }
    }

    public static Entry get(String url) {
        return ENTRIES.get(url);
    }

    public static Entry create(String url, String tag, String mimetype, long timestamp, long expiration) {
        return ENTRIES.put(url, new Entry(url, tag, mimetype, timestamp, expiration));
    }

    public enum Type {
        IMAGE, AUDIO, VIDEO;
    }

    public static final class Entry {
        public final File file;
        public final String url;
        public final String tag;
        public final Type type;
        public final String extension;
        public final long requestTime;
        public final long expiration;

        public Entry(String url, String tag, String mimetype, long requestTime, long expiration) {
            this(url, tag, getTypeExtension(mimetype), requestTime, expiration);
        }

        private Entry(String url, String tag, PairTool<Type, String> typeExtension, long requestTime, long expiration) {
            this.url = url;
            this.file = entry$genFile(this.url);
            this.tag = tag;
            this.type = typeExtension.left();
            this.extension = typeExtension.right();
            this.requestTime = requestTime;
            this.expiration = expiration;
        }

        // TODO: MADE BETTER MIMETYPE VALIDATION USING REGEX
        private static PairTool<Type, String> getTypeExtension(String mimetype) {
            if (mimetype == null)
                throw new IllegalArgumentException("mimetype is null");

            String[] typeExtension = mimetype.split("/");
            if (typeExtension.length == 0) {
                throw new IllegalArgumentException("Mimetype is empty");
            } else {
                return new PairTool<>(Type.valueOf(typeExtension[0]), typeExtension.length == 1 ? "" : typeExtension[1]);
            }
        }

        private void write(DataOutputStream out) throws IOException {
            out.writeUTF(this.url);
            out.writeUTF(this.tag == null ? "null" : this.tag);
            out.writeUTF(this.type.name());
            out.writeUTF(this.extension);
            out.writeLong(this.requestTime);
            out.writeLong(this.expiration);
        }

        public void storeFile(byte[] bytes) throws IOException {
            try (OutputStream out = Files.newOutputStream(file.toPath())) {
                out.write(bytes);
            }
        }

        public void storeFile(InputStream inputStream) throws IOException {
            try (InputStream in = inputStream; OutputStream out = Files.newOutputStream(file.toPath())) {
                int readed;
                byte[] data = new byte[1024 * 4];
                while ((readed = in.read(data)) != -1) {
                    out.write(data, 0, readed);
                }
            }
        }

        public void refresh(String url, String tag, String mimetype, long requestTime, long expiration) {
            ENTRIES.put(url, new Entry(url, tag, mimetype, requestTime, expiration));
        }

        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(file.toPath());
        }

        private static Entry read(DataInputStream in) throws IOException {
            String url = in.readUTF();
            String tag = in.readUTF();
            PairTool<Type, String> typeExtension = new PairTool<>(Type.valueOf(in.readUTF()), in.readUTF());
            long time = in.readLong();
            long expireTime = in.readLong();
            return new Entry(url, !tag.isEmpty() ? tag : null, typeExtension, time, expireTime);
        }

        private static File entry$genFile(String url) {
            String n = DataTool.encodeHex(url);
            return new File(dir, n != null ? n : Base64.getEncoder().encodeToString(url.getBytes()));
        }
    }
}