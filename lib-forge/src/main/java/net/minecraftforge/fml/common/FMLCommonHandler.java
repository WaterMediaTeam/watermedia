package net.minecraftforge.fml.common;

import net.minecraftforge.fml.relauncher.Side;

public class FMLCommonHandler {
    public static FMLCommonHandler instance() { return new FMLCommonHandler(); }
    public Side getSide() {
        return Side.CLIENT;
    }
}
