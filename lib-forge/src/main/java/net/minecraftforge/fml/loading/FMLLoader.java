package net.minecraftforge.fml.loading;

import net.minecraftforge.api.distmarker.Dist;

public class FMLLoader {
    public static Dist getDist() { return Dist.CLIENT; }
    public static boolean isProduction() {
        return false;
    }
    public static LoadingModList getLoadingModList() {
        return new LoadingModList();
    }
}
