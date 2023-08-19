package net.minecraftforge.fml;

public class ModList {
    public static ModList get() {
        return new ModList();
    }

    public boolean isLoaded(String modTarget) {
        return false;
    }
}
