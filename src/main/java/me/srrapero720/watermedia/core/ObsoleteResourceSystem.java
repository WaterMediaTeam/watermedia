package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.util.WaterOs;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class ObsoleteResourceSystem {
    public static void init(IMediaLoader loader) {
        String[] CLONE_TARGETS = new String[] {
                "/pictures/",
                "/vlc/" + WaterOs.getArch() + "/"
        };

        try {
            copiarCarpetaDesdeJar(loader, "", "");
            System.out.println("Carpeta clonada exitosamente en el almacenamiento externo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copiarCarpetaDesdeJar(IMediaLoader loader, String carpetaOrigenDentroJar, String carpetaDestinoExterno) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(carpetaOrigenDentroJar), loader.getJarClassLoader())) {
            Path origenDentroJar = fileSystem.getPath(carpetaOrigenDentroJar);
            Path destinoExterno = Paths.get(carpetaDestinoExterno);


            try (Stream<Path> stream = Files.walk(origenDentroJar)) {
                stream.forEach(source -> {
                            try {
                                Path target = destinoExterno.resolve(origenDentroJar.relativize(source).toString());
                                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }
}
