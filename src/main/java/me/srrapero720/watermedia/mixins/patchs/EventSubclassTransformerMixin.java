package me.srrapero720.watermedia.mixins.patchs;

import net.minecraftforge.eventbus.EventSubclassTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mixin(value = EventSubclassTransformer.class, remap = false, priority = 72)
public class EventSubclassTransformerMixin {
    @Redirect(method = "buildEvents", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;getContextClassLoader()Ljava/lang/ClassLoader;"))
    public ClassLoader redirectBuildEvents(Thread instance) {
        if (instance.getContextClassLoader() == null) {
            LOGGER.warn("Thread.getContextClassLoader() return null, Patched critical NPE highly caused by VLC native threads");
            return ClassLoader.getPlatformClassLoader();
        }
        return instance.getContextClassLoader();
    }
}
