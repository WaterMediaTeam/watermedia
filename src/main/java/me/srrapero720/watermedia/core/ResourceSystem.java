package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.core.exceptions.UnsafeException;
import me.srrapero720.watermedia.util.WaterOs;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

public class ResourceSystem {

    public static void init(IMediaLoader loader) throws UnsafeException {
        // WE CAN REUSE FS FOR OTHER INTERACTIONS OUTSIDE "EXTRACT"
        FileSystem JFS = null;

        try {
            URI jarFileUri = ResourceSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            JFS = FileSystems.newFileSystem(jarFileUri, Collections.emptyMap());

            extract(JFS, loader, "/pictures/", "/vlc/" + WaterOs.getArch() + "/");
        } catch (Exception e) {
            throw new UnsafeException("Failed to extract assets", e);
        } finally {
            IOUtils.closeQuietly(JFS);
        }
    }

    private static void extract(FileSystem system, IMediaLoader loader, String ...assets) throws IOException, URISyntaxException {
        // Ruta al archivo JAR (en este caso, el propio archivo JAR que contiene el código)
        URI jarFileUri = ResourceSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI();

        // Abre el JAR usando el sistema de archivos
        try (FileSystem jarFileSystem = FileSystems.newFileSystem(jarFileUri, Collections.emptyMap(), loader.getJarClassLoader())) {
            for (String asset : assets) {
                Path assetPath = jarFileSystem.getPath(asset);
                if (Files.exists(assetPath)) {
                    // Si el archivo es un directorio, crea el directorio en la ubicación de destino
                    if (Files.isDirectory(assetPath)) {
                        Files.createDirectories(loader.getWorkingDir().resolve(asset));
                    } else {
                        // Si el archivo es un archivo, cópialo a la ubicación de destino
                        Files.copy(assetPath, loader.getWorkingDir().resolve(asset), StandardCopyOption.REPLACE_EXISTING);
                    }

                    System.out.println("Archivo extraído: " + asset);
                } else {
                    System.out.println("No se encontró el archivo: " + asset);
                }
            }
        }
    }
}
