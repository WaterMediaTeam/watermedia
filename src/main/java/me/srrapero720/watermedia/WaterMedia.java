package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.bootstrap.IBootstrap;
import me.srrapero720.watermedia.api.bootstrap.IModuleBootstrap;
import me.srrapero720.watermedia.tools.BufferTool;
import me.srrapero720.watermedia.tools.JarTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

public class WaterMedia {
	public static final Marker IT = MarkerManager.getMarker("Bootstrap");
	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final Logger LOGGER = LogManager.getLogger(ID);

	// RETAINERS
	private static WaterMedia instance;
	private final IBootstrap bootstrap;

	public static WaterMedia getInstance() {
		if (instance == null) throw new IllegalStateException("Instance wasn't created");
		return instance;
	}

	public static WaterMedia init(IBootstrap bootstrap) throws Exception {
		if (bootstrap == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new IllegalStateException("WaterMedia is already loaded");
		return new WaterMedia(bootstrap);
	}

	private WaterMedia(IBootstrap bootstrap) throws Exception {
		LOGGER.info(IT, "Running '{}' on '{}'", NAME, bootstrap.name());
		LOGGER.info(IT, "WaterMedia version '{}'", JarTool.readString("/watermedia/version.cfg"));

		this.bootstrap = bootstrap;
		instance = this;
	}

	public IBootstrap getBootstrap() { return bootstrap; }

	public void init() throws Exception {
		List<IModuleBootstrap> modules = BufferTool.toList(ServiceLoader.load(IModuleBootstrap.class));
		modules.sort(Comparator.comparingInt(e -> e.priority().ordinal()));

		for (IModuleBootstrap m: modules) {
			LOGGER.info(IT, "Starting {}", m.getClass().getSimpleName());
			if (!m.prepare()) {
				LOGGER.warn(IT, "Module {} refuses to be loaded, skipping", m.getClass().getSimpleName());
				continue;
			}
			m.start();
			LOGGER.info(IT, "Module {} loaded successfully", m.getClass().getSimpleName());
		}
		LOGGER.info(IT, "Startup finished");
	}
}