package me.srrapero720.watermedia.minecraft.plugins;

import me.srrapero720.watermedia.WaterMedia;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private static final Marker IT = MarkerFactory.getMarker(MixinPlugin.class.getSimpleName());

    @Override
    public void onLoad(String mixinPackage) {
        WaterMedia.LOGGER.info(IT, "Loading mixins");
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("Forge")) {
            // MIXINS FOR FORGE
        }
        if (mixinClassName.startsWith("Fabric")) {
            // MIXINS FOR FABRIC
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        WaterMedia.LOGGER.info(IT, "Applying mixin {} to target class {}", mixinClassName, targetClassName);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        WaterMedia.LOGGER.info(IT, "Applied mixin {} to target class {}", mixinClassName, targetClassName);
    }
}
