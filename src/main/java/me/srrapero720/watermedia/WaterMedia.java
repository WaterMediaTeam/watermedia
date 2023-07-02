package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.images.LocalStorage;
import me.srrapero720.watermedia.core.lavaplayer.LavaCore;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Marker IT = MarkerFactory.getMarker("Bootstrap");

	public static boolean init() {
		Path storageDirectory = new File(System.getProperty("java.io.tmpdir")).toPath().resolve("watermedia");
		LOGGER.info(IT, "Starting WaterMedia");

		// PREPARE API
		LOGGER.info(IT, "Loading {}", LocalStorage.class.getSimpleName());
		if (!LocalStorage.init(storageDirectory)) return false;

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
		if (!VideoLAN.init(storageDirectory)) return false;

		// PREPARE LAVAPLAYER
		LOGGER.info(IT, "Loading {}", LavaCore.class.getSimpleName());
        if (!LavaCore.init()) return false;

		LOGGER.info(IT, "WaterMedia started successfully");
		return true;
	}

	public static void crashByFVA() {
		throw new IllegalStateException("FancyVideo-API is explicit incompatible with WATERMeDIA, please remove it");
	}
}