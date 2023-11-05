package me.srrapero720.watermedia.tools;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.logging.log4j.Marker;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class IOTool {
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
}