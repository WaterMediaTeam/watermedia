package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 */
@Mod("watermedia")
public class WaterForgeLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");

    public WaterForgeLoader() {
        LOGGER.info(IT, "Running WaterMedia on Forge environment");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::server);

        if (!FMLEnvironment.dist.isDedicatedServer()) WaterMedia.load();

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    void server(FMLDedicatedServerSetupEvent event) {
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
        LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
        LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");

        if (FMLLoader.isProduction()) throw new IllegalStateException("REMOVE WATERMeDIA FROM SERVER_SIDE, THIS IS A CLIENT_SIDE MOD!!!");
        else LOGGER.warn(IT, "Developer environment detected, ignoring crashes");
    }
}