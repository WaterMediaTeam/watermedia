package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MediaURI;
import org.watermedia.core.network.URIPatchException;

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
     * Provides the active status for the patch in case patch has any temporal restriction or runs out for
     * custom online validation on bootstrap
     */
    public abstract boolean active(MediaContext context);

    /**
     * Validates if the {@link MediaURI} can be processed by this patcher
     * @param source MediaSource instance to be patched.
     * @see MediaURI
     * @see MediaURI#get(URI)
     */
    public abstract boolean validate(MediaURI source);

    /**
     * Patches the provided MediaSource
     * @param source URL to patch
     * @throws URIPatchException if URL is null or invalid in this patch
     */
    public abstract void patch(MediaContext context, MediaURI source) throws URIPatchException;

    /**
     * Executes a patch test, validating patch is working and up-to-date
     * Not working patches are not added on the patches registry
     * @param url media url, preferred an HTTP url
     * @param context test context
     */
    public abstract void test(MediaContext context, String url);

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