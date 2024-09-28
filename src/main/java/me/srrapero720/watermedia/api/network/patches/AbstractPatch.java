package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
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
        return this.getClass().getSimpleName();
    }

    /**
     * Platform brand name of the patcher
     * @return brand name. e.j: Twitter (X)
     */
    public abstract String platform();

    /**
     * Check if given {@link MediaURI} can be patched by this patcher
     * @param source MediaSource instance to be patched.
     * @see MediaURI
     * @see MediaURI#get(MediaContext, URI)
     * @return Same provided MediaSource instance but patched
     */
    public abstract boolean validate(MediaURI source);

    /**
     * Patches the provided MediaSource
     * @param source URL to patch
     * @return static URL
     * @throws URIPatchException if URL is null or invalid in this patch
     */
    public abstract MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException;

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