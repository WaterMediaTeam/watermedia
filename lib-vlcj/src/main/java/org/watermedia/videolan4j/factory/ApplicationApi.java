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

package org.watermedia.videolan4j.factory;

import org.watermedia.videolan4j.log.NativeLog;
import org.watermedia.videolan4j.media.TrackType;
import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_module_description_t;

import java.util.ArrayList;
import java.util.List;

/**
 * Behaviour pertaining to the application itself.
 * <p>
 * Includes features such as the native library version and build information, the application, user agent, id and icon,
 * native log, native clock, codec information, and lists of the available native audio and video modules.
 */
public final class ApplicationApi extends BaseApi {

    ApplicationApi(MediaPlayerFactory factory) {
        super(factory);
    }

    /**
     * Get the libvlc version.
     *
     * @return native library version
     */
    public String version() {
        return LibVlc.libvlc_get_version();
    }

    /**
     * Get the compiler used to build libvlc.
     *
     * @return compiler
     */
    public String compiler() {
        return LibVlc.libvlc_get_compiler();
    }

    /**
     * Get the source code change-set id used to build libvlc.
     *
     * @return change-set
     */
    public String changeset() {
        return LibVlc.libvlc_get_changeset();
    }

    /**
     * Set the application name.
     *
     * @param userAgent application name
     */
    public void setUserAgent(String userAgent) {
        setUserAgent(userAgent, null);
    }

    /**
     * Set the application name.
     *
     * @param userAgent application name
     * @param httpUserAgent application name for HTTP
     */
    public void setUserAgent(String userAgent, String httpUserAgent) {
        LibVlc.libvlc_set_user_agent(libvlcInstance, userAgent, httpUserAgent);
    }

    /**
     * Set the application identification information.
     *
     * @param id application id, e.g. com.somecompany.myapp
     * @param version application version
     * @param icon path to application icon
     */
    public void setApplicationId(String id, String version, String icon) {
        LibVlc.libvlc_set_app_id(libvlcInstance, id, version, icon);
    }

    /**
     * Create a new native log component.
     *
     * @return native log component, or <code>null</code> if the native log is not available
     */
    public NativeLog newLog() {
        return new NativeLog(libvlcInstance);
    }

    /**
     * Get the time as defined by LibVLC.
     * <p>
     * The time is not meaningful in the sense of what time it is, rather it is a monotonic clock with an arbitrary
     * starting value.
     *
     * @return current clock time value, in microseconds
     */
    public long clock() {
        return LibVlc.libvlc_clock();
    }

    /**
     * Get a description for a particular codec value.
     *
     * @param type type of track
     * @param codec codec value (or codec FourCC)
     * @return codec description
     */
    public String codecDescription(TrackType type, int codec) {
        return LibVlc.libvlc_media_get_codec_description(type.intValue(), codec);
    }

    /**
     * Get the available audio filters.
     *
     * @return collection of audio filter descriptions
     */
    public List<ModuleDescription> audioFilters() {
        libvlc_module_description_t moduleDescriptions = LibVlc.libvlc_audio_filter_list_get(libvlcInstance);
        List<ModuleDescription> result = getModuleDescriptions(moduleDescriptions);
        LibVlc.libvlc_module_description_list_release(moduleDescriptions.getPointer());
        return result;
    }

    /**
     * Get the available video filters.
     *
     * @return collection of video filter descriptions
     */
    public List<ModuleDescription> videoFilters() {
        libvlc_module_description_t moduleDescriptions = LibVlc.libvlc_video_filter_list_get(libvlcInstance);
        List<ModuleDescription> result = getModuleDescriptions(moduleDescriptions);
        LibVlc.libvlc_module_description_list_release(moduleDescriptions.getPointer());
        return result;
    }

    /**
     * Convert a collection of native module description structures.
     *
     * @param moduleDescriptions module descriptions
     * @return collection of module descriptions
     */
    private List<ModuleDescription> getModuleDescriptions(libvlc_module_description_t moduleDescriptions) {
        List<ModuleDescription> result = new ArrayList<ModuleDescription>();
        libvlc_module_description_t moduleDescription = moduleDescriptions;
        while(moduleDescription != null) {
            result.add(new ModuleDescription(moduleDescription.psz_name, moduleDescription.psz_shortname, moduleDescription.psz_longname, moduleDescription.psz_help));
            moduleDescription = moduleDescription.p_next;
        }
        return result;
    }

}
