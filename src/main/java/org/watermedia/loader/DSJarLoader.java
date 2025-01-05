package org.watermedia.loader;

import org.watermedia.WaterMedia;

import java.nio.file.Path;

public class DSJarLoader implements WaterMedia.ILoader {
    @Override
    public String name() {
        return "DSJL";
    }

    @Override
    public Path tmp() {
        return WaterMedia.DEFAULT_LOADER.tmp();
    }

    @Override
    public Path cwd() {
        return WaterMedia.DEFAULT_LOADER.cwd();
    }

    @Override
    public boolean client() {
        return WaterMedia.DEFAULT_LOADER.client();
    }
}
