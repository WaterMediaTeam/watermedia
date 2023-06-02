package me.srrapero720.watermedia.vlc.provider;

import java.io.File;
import java.nio.file.Path;

import me.srrapero720.watermedia.vlc.extractor.DLLExtractor;
import me.srrapero720.watermedia.vlc.extractor.LuaExtractor;
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class LocalFileProvider implements DiscoveryDirectoryProvider {
    public final Path GAME_DIR;
    public LocalFileProvider(Path gameDir) {
        super();
        this.GAME_DIR = gameDir;
    }
    
    @Override public int priority() { return 5; }
    @Override public boolean supported() { return true; }
    @Override public String[] directories() {
        var vlc = GAME_DIR.resolve("cache/vlc/");
        LOGGER.info("Path from file extraction is '{}'", vlc.toAbsolutePath().toFile());

        // HAY QUE REVISAR SI YA EXISTEN BINARIOS O SI HAY QUE ACTUALIZARLOS
        // SOLO DEBE EXTRAER LOS BINARIOS SI HAY QUE ACTUALIZAR LOS BINARIOS (SI EXISTEN ARCHIVOS DEBE BORRARLOS Y EXTRAER)
        // O SI NO EXISTEN ARCHIVOS.
        // CASO CONTRARIO NO DEBE HACER NADA
        // TODO: Crear un archivo de configuracion y que este determine si debe re-extraer los bins
        if (true) {
            LOGGER.warn("Extracting BINS before VLCJ attempt to load local files");
            for (var binary: DLLExtractor.values()) binary.extract();

            LOGGER.warn("Also extracting LUA scripts");
            for (var luac: LuaExtractor.values()) luac.extract();
        } else {
            LOGGER.warn("Local bins detected, ignoring extractor");
        }

        return new String[] { vlc.toAbsolutePath().toString() };
    }
}