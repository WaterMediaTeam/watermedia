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

import org.watermedia.videolan4j.binding.lib.LibVlc;
import org.watermedia.videolan4j.binding.internal.libvlc_equalizer_t;
import org.watermedia.videolan4j.player.base.Equalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Behaviour pertaining to the audio equalizer.
 */
public final class EqualizerApi extends BaseApi {

    private final List<Float> bands;

    private final List<String> presets;

    EqualizerApi(MediaPlayerFactory factory) {
        super(factory);
        this.bands = cacheBands();
        this.presets = cachePresets();
    }

    /**
     * Get the list of distinct equalizer band frequencies.
     *
     * @return list of frequencies (Hz)
     */
    public final List<Float> bands() {
        return new ArrayList<Float>(bands);
    }

    /**
     * Get the list of names of available equalizer presets.
     *
     * @return list of preset names
     */
    public final List<String> presets() {
        return new ArrayList<String>(presets);
    }

    /**
     * Create a new audio equalizer.
     *
     * @return equalizer
     */
    public final Equalizer newEqualizer() {
        return new Equalizer(LibVlc.libvlc_audio_equalizer_get_band_count());
    }

    /**
     * Create a new audio equalizer from a named preset.
     *
     * @param presetName name of the preset
     * @return equalizer
     */
    public final Equalizer newEqualizer(String presetName) {
        int index = presets.indexOf(presetName);
        if (index != -1) {
            libvlc_equalizer_t presetEqualizer = LibVlc.libvlc_audio_equalizer_new_from_preset(index);
            if(presetEqualizer != null) {
                Equalizer equalizer = new Equalizer(LibVlc.libvlc_audio_equalizer_get_band_count());
                equalizer.setPreamp(LibVlc.libvlc_audio_equalizer_get_preamp(presetEqualizer));
                for(int i = 0; i < LibVlc.libvlc_audio_equalizer_get_band_count(); i++) {
                    equalizer.setAmp(i, LibVlc.libvlc_audio_equalizer_get_amp_at_index(presetEqualizer, i));
                }
                LibVlc.libvlc_audio_equalizer_release(presetEqualizer);
                return equalizer;
            }
            else {
                return null;
            }
        }
        else {
            throw new IllegalArgumentException("No such preset named '" + presetName + "'");
        }
    }

    /**
     * Get all of the available preset equalizer instances.
     * <p>
     * This will return new equalizer instances (i.e. they are not cached or shared), so applications are free to change
     * the values in the returned equalizer instances if so desired.
     *
     * @return map of preset name to equalizer instance, sorted by name
     */
    public final Map<String, Equalizer> allPresetEqualizers() {
        Map<String, Equalizer> result = new TreeMap<String, Equalizer>();
        for (String preset : presets) {
            result.put(preset, newEqualizer(preset));
        }
        return result;
    }

    private List<Float> cacheBands() {
        int numBands = LibVlc.libvlc_audio_equalizer_get_band_count();
        List<Float> result = new ArrayList<Float>(numBands);
        for (int i = 0; i < numBands; i++) {
            result.add(LibVlc.libvlc_audio_equalizer_get_band_frequency(i));
        }
        return Collections.unmodifiableList(result);
    }

    private List<String> cachePresets() {
        int numPresets = LibVlc.libvlc_audio_equalizer_get_preset_count();
        List<String> result = new ArrayList<String>(numPresets);
        for (int i = 0; i < numPresets; i++) {
            result.add(LibVlc.libvlc_audio_equalizer_get_preset_name(i));
        }
        return Collections.unmodifiableList(result);
    }

}
