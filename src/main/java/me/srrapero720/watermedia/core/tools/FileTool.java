package me.srrapero720.watermedia.core.tools;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class FileTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static String readString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    public static GifDecoder readGif(Path path) {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path.toFile().toPath()))) {
            GifDecoder gif = new GifDecoder();
            int status = gif.read(in);
            if (status == GifDecoder.STATUS_OK) return gif;

            throw new IOException("Failed to process GIF - Decoder status: " + status);
        } catch (Exception e) {
            LOGGER.error(IT, "Failed loading GIF from WaterMedia resources", e);
        }
        return null;
    }

    public static void un7zip(Path zipFilePath) throws IOException { un7zip(zipFilePath, zipFilePath.getParent()); }

    public static void un7zip(Path zipFilePath, Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdir();

//        try (SevenZFile sevenZFile = new SevenZFile(zipFilePath.toFile(), "watermedia-is-my-lord".getBytes(StandardCharsets.UTF_16LE))) {
        try (SevenZFile sevenZFile = new SevenZFile(zipFilePath.toFile())) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();

            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (entry.isDirectory()) {
                    new File(filePath).mkdirs();
                } else {
                    un7zip$extract(sevenZFile, filePath);
                }

                entry = sevenZFile.getNextEntry();
            }
        }
    }

    private static void un7zip$extract(SevenZFile sevenZFile, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] content = new byte[4096];
            int read;
            while ((read = sevenZFile.read(content)) != -1) bos.write(content, 0, read);
        }
    }

    public static void unzip(Path zipFilePath) throws IOException { unzip(zipFilePath, zipFilePath.getParent()); }
    public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdir();

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath.toFile().toPath()))) {
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
        }
    }
    private static void unzip$extract(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) bos.write(bytesIn, 0, read);
        }
    }
}