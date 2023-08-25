package me.srrapero720.watermedia.core.tools;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class FileTool {
    private static final Marker IT = MarkerManager.getMarker(FileTool.class.getSimpleName());

    public static String readString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    public static GifDecoder readGif(Path path) {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            GifDecoder gif = new GifDecoder();
            int status = gif.read(in);
            if (status == GifDecoder.STATUS_OK) return gif;

            throw new IOException("Failed to process GIF - Decoder status: " + status);
        } catch (Exception e) {
            LOGGER.error(IT, "Failed loading GIF from WaterMedia resources", e);
        }
        return null;
    }

    public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath.toFile()));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                unzip$extract(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void unzip$extract(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}