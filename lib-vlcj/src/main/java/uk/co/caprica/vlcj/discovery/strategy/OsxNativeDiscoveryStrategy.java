/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.discovery.strategy;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import uk.co.caprica.vlcj.VideoLan4J;
import uk.co.caprica.vlcj.binding.lib.LibC;
import uk.co.caprica.vlcj.discovery.provider.DirectoryProviderDiscoveryStrategy;

/**
 * Default implementation of a native discovery strategy that searches directories on the OSX operating system.
 */
public class OsxNativeDiscoveryStrategy extends DirectoryProviderDiscoveryStrategy {

    private static final String[] FILENAME_PATTERNS = new String[] {
        "libvlc\\.dylib",
        "libvlccore\\.dylib"
    };

    /**
     * Format string to prepare the plugin path environment variable value.
     */
    private static final String[] PLUGIN_PATH_FORMATS = new String[] {
        "%s/../plugins"
    };

    public OsxNativeDiscoveryStrategy() {
        super(FILENAME_PATTERNS, PLUGIN_PATH_FORMATS);
    }

    @Override
    public boolean supported() {
        return Platform.isMac();
    }

    @Override
    public boolean onFound(String path) { // WATERMeDIA Patch: uhhhhhhhh
        try {
            forceLoadLibVlcCore(path);
        } catch (Error e) {
            return false;
        }
        return true;
    }

    /**
     * On later versions of OSX, it is necessary to force-load libvlccore before libvlc, otherwise libvlc will fail to
     * load.
     *
     * @param path
     */
    private void forceLoadLibVlcCore(String path) {
        NativeLibrary.addSearchPath(VideoLan4J.LIBVLCCORE_NAME, path);
        NativeLibrary.getInstance(VideoLan4J.LIBVLCCORE_NAME);
    }

    @Override
    protected boolean setPluginPath(String pluginPath) {
        return LibC.INSTANCE.setenv(PLUGIN_ENV_NAME, pluginPath, 1) == 0;
    }

}
