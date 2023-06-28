package me.lib720.caprica.vlcj.factory.discovery;

import com.sun.jna.NativeLibrary;
import com.sun.jna.StringArray;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import me.lib720.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj.support.version.LibVlcVersion;
import me.srrapero720.watermedia.Util;
import me.lib720.caprica.vlcj.binding.internal.libvlc_instance_t;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;
import static me.lib720.caprica.vlcj.binding.lib.LibVlc.libvlc_new;
import static me.lib720.caprica.vlcj.binding.lib.LibVlc.libvlc_release;

/**
 * Native library discovery component.
 * <p>
 * This component attempts to automatically locate the LibVLC native library so that it may be reliably loaded by JNA.
 * <p>
 * The intent is that native discovery "just works". To that end, a number of default {@link NativeDiscoveryStrategy}
 * implementations are provided.
 * <p>
 * It is possible to provide your own replacement native discovery strategy implementations when creating an instance of
 * this component, or alternatively to provide none at all to sidestep automatic native discovery.
 * <p>
 * The first discovery strategy implementation that reports that it has found the native libraries "wins" - this means
 * that even if turns out subsequently that the native library can <em>not</em> be loaded via this strategy, any other
 * remaining discovery strategies are <em>not</em> tried.
 * <p>
 * It is possible that even if native discovery fails, i.e. {@link #discover()} returns <code>false</code>, that the
 * native library can be loaded successfully. This could happen in an environment that is already well-configured, with
 * libraries installed in places that the Operating System and JVM already know about.
 * <p>
 * In {@link MediaPlayerFactory}, where this native discovery component is primarily used, an attempt to load the native
 * library will <em>always</em> be made, whether explicit discovery works or not.
 * <p>
 * This behaviour is by design, as is always trying the discovery first whether or not this "default" library loading
 * would work - since it is possible that a client application does not actually want to prioritise the default library
 * load (e.g. different version of VLC if multiple are installed).
 */
public class NativeDiscovery {

    /**
     * Name of the system environment variable containing the VLC plugin path location.
     * <p>
     * This is optional, and might not be set.
     */
    protected static final String PLUGIN_ENV_NAME = "VLC_PLUGIN_PATH";

    /**
     * Flag if the discovery already completed and found the native libraries.
     * <p>
     * There is no point running the discovery again if the libraries were already found, since the native library
     * search path will already have been set and a successful discovery would do no more than set it again.
     * <p>
     * If the discovery failed before, then running it again may work e.g. if the client application took some remedial
     * steps to make the native libraries available.
     */
    private static boolean alreadyFound;

    /**
     * The native discovery strategy instance that discovered the native library path.
     */
    private NativeDiscoveryStrategy successfulStrategy;

    /**
     * The native library path that was discovered.
     */
    private String discoveredPath;

    private static final NativeDiscoveryStrategy[] DEFAULT_STRATEGIES = new NativeDiscoveryStrategy[] {
            new WindowsNativeDiscoveryStrategy(),
            new OsxNativeDiscoveryStrategy(),
            new LinuxNativeDiscoveryStrategy(),
    };

    /**
     * Strategy implementations.
     */
    private final NativeDiscoveryStrategy[] discoveryStrategies;

    /**
     * Create a native discovery component.
     * <p>
     * If any strategies are supplied via this constructor, they <em>entirely replace</em> the defaults.
     *
     * @param discoveryStrategies zero or more native discovery strategy implementations
     */
    public NativeDiscovery(NativeDiscoveryStrategy... discoveryStrategies) {
        this.discoveryStrategies = discoveryStrategies.length > 0 ? discoveryStrategies : DEFAULT_STRATEGIES;
    }

    /**
     * Perform native library discovery.
     * <p>
     * Discovery will stop when a strategy returns a discovered location - it is still possible that the native library
     * will fail to load, but even if does not load there is no chance to resume discovery with that strategy or any of
     * the subsequent ones (due to how {@link NativeLibrary#addSearchPath(String, String)} works).
     * <p>
     * If this component has already discovered the native libraries, calling this method again will have no effect.
     *
     * @return is discovered
     */
    public final boolean discover() {
        if (alreadyFound) {
            return true;
        } else {
            for (NativeDiscoveryStrategy discoveryStrategy : discoveryStrategies) {
                if (discoveryStrategy.supported()) {
                    String path = discoveryStrategy.discover();
                    if (path != null) {
                        LOGGER.info("Attempt to load VLC on '{}'", path);
                        if (discoveryStrategy.onFound(path)) {
                            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
                        }
                        tryPluginPath(path, discoveryStrategy);
                        if (tryLoadingLibrary()) {
                            successfulStrategy = discoveryStrategy;
                            discoveredPath = path;
                            onFound(path, discoveryStrategy);
                            alreadyFound = true;
                            return true;
                        } else {
                            // Tries to fix searchpaths
                            LOGGER.error(IT, "Failed loading VLC in '{}' using '{}' cleaning JNA", path, discoveryStrategy.getClass().getSimpleName());
                            if (attemptFix(path, discoveryStrategy)) continue;
                            // We have to stop here, because we already added a search path for the native library and
                            // any further search paths we add will be tried AFTER the one that already failed - the
                            // subsequent directories we may like to try will never actually be tried
                            onFailed(path, discoveryStrategy);
                            return false;
                        }
                    }
                }
            }
            onNotFound();
            return false;
        }
    }

    private static Field searchPaths;
    private static Field libraries;
    private static final Marker IT = MarkerFactory.getMarker("VideoLAN");
    @SuppressWarnings("unchecked")
    public boolean attemptFix(String path, NativeDiscoveryStrategy discoveryStrategy) {
        if (searchPaths == null) {
            searchPaths = Util.getClassField(NativeLibrary.class, "searchPaths");
            libraries = Util.getClassField(NativeLibrary.class, "libraries");
        }
        try {
            Map<String, Reference<NativeLibrary>> libs = (Map<String, Reference<NativeLibrary>>) libraries.get(null);
            Map<String, List<String>> paths = (Map<String, List<String>>) searchPaths.get(null);
            libs.remove(RuntimeUtil.getLibVlcCoreLibraryName());
            paths.remove(RuntimeUtil.getLibVlcCoreLibraryName());
            libs.remove(RuntimeUtil.getLibVlcLibraryName());
            paths.remove(RuntimeUtil.getLibVlcLibraryName());
            return true;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(IT, "attemptFix failed", e);
        }
        return false;
    }


    /**
     * Get the native discovery strategy instance that discovered the native library.
     * <p>
     * Used only for diagnostic purposes.
     *
     * @return strategy instance
     */
    public final NativeDiscoveryStrategy successfulStrategy() {
        return successfulStrategy;
    }

    /**
     * Get the discovered native library path.
     * <p>
     * Used only for diagnostic purposes.
     *
     * @return native library path
     */
    public final String discoveredPath() {
        return discoveredPath;
    }

    /**
     * Set the VLC_PLUGIN_PATH environment variable to point to the plugins directory of the discovered native library
     * path.
     * <p>
     * Rather than setting the plugin path here, we must ask the strategy to set the path. This is because there are
     * different ways (different native API) to set process environment variables on e.g. Linux vs Windows.
     *
     * @param path path where the native libraries were discovered
     * @param discoveryStrategy discovery strategy that found the native libraries
     */
    private void tryPluginPath(String path, NativeDiscoveryStrategy discoveryStrategy) {
        String env = System.getenv(PLUGIN_ENV_NAME);
        if (env == null || env.length() == 0) {
            // The return value from onSetPluginPath is currently not used (it would imply that the API call to set the
            // process environment variable failed, which is somewhat of a stretch that it would ever occur)
            discoveryStrategy.onSetPluginPath(path);
        }
    }

    /**
     * Attempt to load the native library.
     * <p>
     * This is done immediately after discovery so that any error condition can be handled as early as possible.
     */
    private boolean tryLoadingLibrary() {
        try {
            libvlc_instance_t instance = libvlc_new(0, new StringArray(new String[0]));
            if (instance != null) {
                libvlc_release(instance);
                LibVlcVersion version = new LibVlcVersion();
                if (version.isSupported()) {
                    return true;
                }
            }
        }
        catch (UnsatisfiedLinkError e) {
            // The library could not be loaded, this includes NoClassDefFoundError which would be thrown e.g. if there
            // was a direct-mapped method in the LibVlc class that was missing from the loaded native library - we don't
            // report the error here (since this discovery is optional), it will be reported by the factory subsequently

            // This message should display the reason the native library could not be bound, specifically if the library
            // binding failed due to an undefined symbol it should be displayed here
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Template method invoked when the native libraries were successfully found.
     * <p>
     * Sub-classes can override this method to provide bespoke behaviour after the native library was successfully
     * loaded.
     *
     * @param path path where the native libraries were discovered
     * @param strategy discovery strategy that found the native libraries
     */
    protected void onFound(String path, NativeDiscoveryStrategy strategy) {
        LOGGER.info(IT, "Located VLC in '{}' using '{}'", path, strategy.getClass().getSimpleName());
    }

    /**
     * Template method invoked if the native library could not be loaded from the discovered location.
     * <p>
     * Sub-classes can override this method to provide bespoke behaviour when the native library failed to load.
     *
     * @param path path where the native library were discovered
     * @param strategy discovery strategy that found, but failed to load, the native library
     */
    protected void onFailed(String path, NativeDiscoveryStrategy strategy) {
        LOGGER.info(IT, "Failed to load VLC in '{}' using '{}'", path, strategy.getClass().getSimpleName());
    }

    /**
     * Template method invoked if the native libraries could not be found by any known discovery strategy.
     */
    protected void onNotFound() {
        LOGGER.info(IT, "VLC cannot be found/loaded");
    }

}
