package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.IMediaLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

public class ClonarCarpetaDesdeJAR {

    public static void main(String[] args) {
        String carpetaOrigenDentroJar = "/carpeta_a_clonar"; // Ruta dentro del JAR de la carpeta que deseas clonar
        String carpetaDestinoExterno = "ruta_del_almacenamiento_externo"; // Ruta del almacenamiento externo

        try {
            copiarCarpetaDesdeJar(carpetaOrigenDentroJar, carpetaDestinoExterno);
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
