package me.srrapero720.watermedia.loader;

import java.io.File;
import java.nio.file.Path;
import me.srrapero720.watermedia.WaterMedia;

/**
 * Custom impl for {@link WaterMedia} loaders
 * by default contains impl for Minecraft modloaders like MinecraftForge, NeoForge and Fabric
 */
public interface ILoader {
    Path TMP_DEFAULT = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    Path CWD_DEFAULT = new File("run").toPath().toAbsolutePath();

    /**
     * Name of the loader, preferable the bootstrap name
     * @return loader name
     */
    String name();

    /**
     * Tmp path is normally java.io.tmp directory; you can set up your custom tmp dir,
     * @return Path instance to tmp directory
     */
    Path tmp();

    /**
     * Absolute path to current working directory
     * @return Current working directory
     */
    Path cwd();

    /**
     * Check if the current environment is client side
     * @return if was client-side
     */
    boolean client();

    /**
     * Default {@link ILoader} instance to be used in non-minecraft instances
     * @see ILoader
     */
    ILoader DEFAULT = new ILoader() {
        @Override public String name() { return "Default"; }
        @Override public Path tmp() { return TMP_DEFAULT; }
        @Override public Path cwd() { return CWD_DEFAULT; }
        @Override public boolean client() { return true; }
    };
}