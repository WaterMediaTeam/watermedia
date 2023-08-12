package me.srrapero720.watermedia.core.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileTool {
    public static String readString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
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

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void unzip$extract(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
