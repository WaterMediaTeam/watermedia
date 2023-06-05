package me.srrapero720.watermedia.api.media.compat;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class SoundCloudCompat extends CompatVideoUrl {
    @Override
    public boolean isValid(@NotNull URL url) {
        return false;
    }
}
