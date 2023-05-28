package me.srrapero720.watermedia.vlc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.util.Enumeration;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ResourceExtractor {
    public static final String resourcePath = "/VLC/windows/amd64";
    public static final String outputDirectory = "/cache/vlc";

    public static void extract(File gameDir) {
        try {
            File outputDir = new File(gameDir.getAbsolutePath() + outputDirectory);
            if (!outputDir.exists()) {
                Files.createDirectories(outputDir.toPath());
            }

            extractFilesFromResource(resourcePath, outputDir);

            LOGGER.info("Bins extracted on: " + outputDirectory);
        } catch (IOException e) {
            LOGGER.error("Failed to extract bins", e);
        }
    }

    private static void extractFilesFromResource(String resourcePath, File outputDir) throws IOException {
        Enumeration<URL> resources = ResourceExtractor.class.getClassLoader().getResources(resourcePath);
        while (resources.hasMoreElements()) {
            URL resourceUrl = resources.nextElement();
            if (resourceUrl.getProtocol().equals("jar")) {
                extractFromJar(resourceUrl, resourcePath, outputDir);
            } else {
                extractFromFileSystem(resourceUrl, resourcePath, outputDir);
            }
        }
    }

    private static void extractFromJar(URL resourceUrl, String resourcePath, File outputDir) throws IOException {
        try (InputStream inputStream = resourceUrl.openStream()) {
            String[] parts = resourceUrl.getPath().split("!");
            String jarPath = parts[0];
            String filePath = parts[1].substring(1); // Remove the leading "/"

            File tempJarFile = File.createTempFile("temp", ".jar");
            tempJarFile.deleteOnExit();

            FileUtils.copyURLToFile(new URL(jarPath), tempJarFile);

            try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(tempJarFile)) {
                Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    java.util.jar.JarEntry entry = entries.nextElement();
                    String entryPath = entry.getName();

                    if (entryPath.startsWith(filePath) && !entry.isDirectory()) {
                        extractFileFromJar(jarFile, entry, entryPath, outputDir);
                    }
                }
            }
        }
    }

    private static void extractFileFromJar(java.util.jar.JarFile jarFile, java.util.jar.JarEntry entry, String entryPath, File outputDir) throws IOException {
        String fileName = entryPath.substring(entryPath.lastIndexOf('/') + 1);
        File outputFile = new File(outputDir, fileName);

        try (InputStream inputStream = jarFile.getInputStream(entry)) {
            Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void extractFromFileSystem(URL resourceUrl, String resourcePath, File outputDir) throws IOException {
        File resourceDir = new File(resourceUrl.getFile());
        File[] files = resourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                extractFileFromFileSystem(file, outputDir);
            }
        }
    }

    private static void extractFileFromFileSystem(File file, File outputDir) throws IOException {
        String fileName = file.getName();
        File outputFile = new File(outputDir, fileName);

        if (file.isDirectory()) {
            FileUtils.copyDirectory(file, outputFile);
        } else {
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
