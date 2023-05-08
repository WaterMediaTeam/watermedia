package me.srrapero720.watermedia.mixin;

import com.sun.jna.NativeLibrary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;

@Mixin(NativeDiscovery.class)
public abstract class MixinNativeDiscovery {

    @Shadow private static boolean alreadyFound;
    @Final
    @Shadow private Iterable<NativeDiscoveryStrategy> discoveryStrategies;
    @Shadow private NativeDiscoveryStrategy successfulStrategy;
    @Shadow private String discoveredPath;

    @Shadow protected abstract void onFound(String path, NativeDiscoveryStrategy strategy);

    @Shadow protected abstract void onFailed(String path, NativeDiscoveryStrategy strategy);

    @Shadow protected abstract void onNotFound();

    @Shadow protected abstract void tryPluginPath(String path, NativeDiscoveryStrategy discoveryStrategy);

    @Shadow protected abstract boolean tryLoadingLibrary();

    @Inject(method = "discover", at = @At(value = "INVOKE", target = "Luk/co/caprica/vlcj/factory/discovery/NativeDiscovery;tryLoadingLibrary()Z", ordinal = 0, remap = false), cancellable = true)
    private void injectDiscover(CallbackInfoReturnable<Boolean> cir) {
        if (!alreadyFound) {
            for (NativeDiscoveryStrategy discoveryStrategy : discoveryStrategies) {
                if (discoveryStrategy.supported()) {
                    String path = discoveryStrategy.discover();
                    if (path != null) {
                        if (discoveryStrategy.onFound(path)) {
                            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
                        }
                        tryPluginPath(path, discoveryStrategy);
                        if (tryLoadingLibrary()) {
                            successfulStrategy = discoveryStrategy;
                            discoveredPath = path;
                            onFound(path, discoveryStrategy);
                            alreadyFound = true;
                            cir.setReturnValue(true);
                            return;
                        } else {
                            if (attemptFix(path, discoveryStrategy))
                                continue;
                            // We have to stop here, because we already added a search path for the native library and
                            // any further search paths we add will be tried AFTER the one that already failed - the
                            // subsequent directories we may like to try will never actually be tried
                            onFailed(path, discoveryStrategy);
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }
            }
            onNotFound();
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(true);
        }
    }

    public boolean attemptFix(String path, NativeDiscoveryStrategy discoveryStrategy) {
        // Your implementation here
        return false;
    }
}