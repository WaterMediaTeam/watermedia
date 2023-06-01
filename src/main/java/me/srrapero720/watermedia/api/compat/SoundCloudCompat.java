package me.srrapero720.watermedia.api.compat;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class SoundCloudCompat extends AbstractCompat {
    @Override
    public boolean isValid(@NotNull URL url) {
        return false;
    }
}
