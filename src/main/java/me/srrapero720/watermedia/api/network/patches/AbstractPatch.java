package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaModContext;
import me.srrapero720.watermedia.api.uri.MediaSource;
import me.srrapero720.watermedia.api.network.URIPatchException;

import java.net.URI;

/**
 * Service class
 *
 */
public abstract class AbstractPatch {

    /**
     * Get the name patch name
     * @return class name by default
     */
    public String name() {
        return getClass().getSimpleName();
    }

    /**
     * Platform brand name of the patcher
     * @return brand name. e.j: Twitter (X)
     */
    public abstract String platform();

    /**
     * Check if given {@link MediaSource} can be patched by this patcher
     * @param source MediaSource instance to be patched.
     * @see MediaSource
     * @see MediaSource#get(MediaModContext, URI)
     * @return Same provided MediaSource instance but patched
     */
    public abstract boolean validate(MediaSource source);

    /**
     * Patches the provided MediaSource
     * @param source URL to patch
     * @return static URL
     * @throws URIPatchException if URL is null or invalid in this patch
     */
    public abstract MediaSource patch(MediaSource source, MediaModContext context) throws URIPatchException;

    /**
     * Returns the name of the Fixer
     * @return redirect call to {@link #name()}
     * @see #name()
     */
    @Override
    public String toString() {
        return name();
    }
}