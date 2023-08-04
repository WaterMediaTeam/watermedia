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
import java.util.function.BiPredicate;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * This class is busted
 * is usefull to auto extract a list of files from JAR but doesn't works on Forge (fuck you cpw)
 * BTW, I made a... hacky way do stop lising binaries(?
 */
public class ResourceSystem {

    public static void init(IMediaLoader loader) throws UnsafeException {
        // WE CAN REUSE FS FOR OTHER INTERACTIONS OUTSIDE "EXTRACT"
        FileSystem JFS = null;

        try {
            URI jarFileUri = ResourceSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            LOGGER.warn("uri {}", jarFileUri.toString());

            // PAYLOAD
            Map<String, Object> payload = new HashMap<>();
            payload.put("filter", (BiPredicate<Object, Object>) (o, o2) -> true);

            JFS = FileSystems.newFileSystem(jarFileUri, payload, loader.getJarClassLoader());
            extract(JFS, loader, "/pictures/", "/vlc/" + WaterOs.getArch() + "/");
        } catch (Exception e) {
            throw new UnsafeException("Failed to extract assets", e);
        } finally {
            IOUtils.closeQuietly(JFS);
        }
    }

    private static void extract(FileSystem system, IMediaLoader loader, String ...assets) throws IOException, URISyntaxException {
        // Abre el JAR usando el sistema de archivos
        for (String asset : assets) {
            Path assetPath = system.getPath(asset);
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
