package me.srrapero720.watermedia.tools;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class IOTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static String readString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    public static GifDecoder readGif(Marker it, Path path) throws IOException {
        LOGGER.debug(it, "Reading gif from '{}'", path);
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path.toFile().toPath()))) {
            return new GifDecoder().readOrThrow(in);
        }
    }

    public static void un7zip(Path zipFilePath) throws IOException { un7zip(zipFilePath, zipFilePath.getParent()); }
    public static void un7zip(Path zipFilePath, Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdir();

//        try (SevenZFile sevenZFile = new SevenZFile(zipFilePath.toFile(), "watermedia-is-my-lord".getBytes(StandardCharsets.UTF_16LE))) {
        try (SevenZFile sevenZFile = new SevenZFile(zipFilePath.toFile())) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();

            while (entry != null) {
                File outputFile = new File(destDirectory + File.separator + entry.getName());
                if (!outputFile.exists()) {
                    if (entry.isDirectory()) {
                        if (outputFile.mkdirs()) LOGGER.error(IT, "Cannot create directories of '{}'", entry.getName());
                    } else {
                        un7zip$extract(sevenZFile, outputFile.toPath());
                    }
                } else {
                    LOGGER.warn(IT, "Cancelled un7zip attempt of '{}', file already exists", entry.getName());
                }

                entry = sevenZFile.getNextEntry();
            }
        }
    }

    private static void un7zip$extract(SevenZFile sevenZFile, Path filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {
            byte[] content = new byte[4096 * 4]; // MEMORY AHEAD
            int read;
            while ((read = sevenZFile.read(content)) != -1) bos.write(content, 0, read);
        }
    }


    public static void unzip(Marker it, Path zipFilePath) throws IOException { unzip(it, zipFilePath, zipFilePath.getParent()); }
    public static void unzip(Marker it, Path zipFilePath, Path destDirectory) throws IOException {
        LOGGER.debug(it, "Unzipping file from '{}' to directory '{}'", zipFilePath, destDirectory);
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
        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) output.write(bytesIn, 0, read);
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String encodeHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}