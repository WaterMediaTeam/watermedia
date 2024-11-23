package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.WaterMedia;
import org.watermedia.api.network.MRL;
import org.watermedia.tools.NetTool;

import java.net.URI;

/**
 * Service class
 *
 */
public abstract class AbstractPatch {

    /**
     * Get the name patch name
     *
     * @return class name by default
     */
    public String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * Platform brand name of the patcher
     *
     * @return brand name. e.j: Twitter (X)
     */
    public abstract String platform();

    /**
     * Get the valid user agent for this source
     *
     * @return WaterMedia user agent by default
     */
    public String userAgent() {
        return WaterMedia.USER_AGENT;
    }

    /**
     * Provides the active status for the patch in case patch has any temporal restriction or runs out for
     * custom online validation on bootstrap
     */
    public abstract boolean active(MediaContext context);

    /**
     * Validates if the {@link MRL} can be processed by this patcher
     *
     * @param source MediaSource instance to be patched.
     * @see MRL
     * @see MRL#get(URI)
     */
    public abstract boolean validate(MRL source);

    /**
     * Patches the provided MediaSource
     *
     * @param source URL to patch
     * @throws PatchException if URL is null or invalid in this patch
     */
    public abstract void patch(MediaContext context, MRL source) throws PatchException;

    /**
     * Executes a patch test, validating patch is working and up-to-date
     * Not working patches are not added on the patches registry
     *
     * @param url media url, preferred an HTTP url
     * @param context test context
     */
    public abstract void test(MediaContext context, String url);

    /**
     * Returns the name of the Fixer
     *
     * @return redirect call to {@link #name()}
     * @see #name()
     */
    @Override
    public String toString() {
        return name();
    }

    public static class PatchException extends Exception {
        public PatchException(String uri, String message) {
            super("Failed to patch URI '" + uri + "'; " + message);
        }

        public PatchException(String uri, Exception e) {
            super("Failed to patch URI '" + uri + "'; " + e.getLocalizedMessage(), e);
        }

        public PatchException(URI source, String message) {
            this(source.toString(), message);
        }

        public PatchException(URI source, Exception message) {
            this(source.toString(), message);
        }

        public PatchException(MRL source, String message) {
            this(source.getUri(), message);
        }

        public PatchException(MRL source, Exception e) {
            this(source.getUri(), e);
        }
    }
}