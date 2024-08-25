package me.srrapero720.watermedia.tools;

import me.srrapero720.watermedia.api.image.decoders.GifDecoder;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
            return new String(bytes, StandardCharsets.UTF_8);
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

    public static boolean rmdirs(Path path) {
        return rmdirs(path.toFile());
    }

    public static boolean rmdirs(File root) {
        File[] files = root.listFiles();

        if (files == null || files.length == 0) return root.delete();
        for (File f: files) {
            File[] childs = f.listFiles();
            if (childs != null && childs.length != 0 && !rmdirs(f)) return false;
            if (!f.delete()) return false;
        }
        return true;
    }

    public static void un7zip(Marker it, Path zipPath) throws IOException { un7zip(it, zipPath, zipPath.getParent()); }
    public static void un7zip(Marker it, Path zipPath, Path destPath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(zipPath.toFile(), "r");
             IInArchive archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file))) {

            ISimpleInArchive simpleInArchive = archive.getSimpleInterface();
            for (ISimpleInArchiveItem i: simpleInArchive.getArchiveItems()) {
                Path destination = destPath.resolve(i.getPath());
                if (i.isFolder()) {
                    if (!destination.toFile().mkdirs()) {
                        LOGGER.error(it, "Failed to create directory '{}'", destination.toAbsolutePath().toString());
                    }
                    continue;
                }
                ExtractOperationResult result;

                result = i.extractSlow(data -> {
//                    BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(destination));
                    return data.length; // Return amount of consumed data
                });

                if (result != ExtractOperationResult.OK) {
                    throw new IOException("Failed to extract file '"+ destination.toAbsolutePath() + "', status code: " + result.name());
                }
            }
        }

    }

    public static void unzip(Marker it, Path zipFilePath) throws IOException { unzip(it, zipFilePath, zipFilePath.getParent()); }
    public static void unzip(Marker it, Path zipFilePath, Path destDirectory) throws IOException {
        LOGGER.debug(it, "Unzipping file from '{}' to directory '{}'", zipFilePath, destDirectory);
        if (zipFilePath.toString().endsWith(".7z")) throw new IOException("Attempted to extract a 7z as a .zip");
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath))) {
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