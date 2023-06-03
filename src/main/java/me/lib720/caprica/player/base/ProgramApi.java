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
 * Copyright 2009-2022 Caprica Software Limited.
 */

package me.lib720.caprica.player.base;

import me.lib720.caprica.binding.lib.LibVlc;
import me.lib720.caprica.binding.internal.libvlc_player_program_t;
import me.lib720.caprica.binding.internal.libvlc_player_programlist_t;
import me.lib720.caprica.binding.support.types.size_t;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Behaviour pertaining to media player programs.
 */
public final class ProgramApi extends BaseApi {

    ProgramApi(MediaPlayer mediaPlayer) {
        super(mediaPlayer);
    }

    /**
     * This returns a snapshot of the program list.
     * <p>
     * If any program changed events are received, this list should be released via {@link #release()} and a further
     * call to this method made to get the updated list.
     *
     * @return program list
     */
    public List<Program> list() {
        libvlc_player_programlist_t programList = LibVlc.libvlc_media_player_get_programlist(mediaPlayerInstance);
        if (programList != null) {
            int count = LibVlc.libvlc_player_programlist_count(programList).intValue();
            List<Program> result = new ArrayList<Program>(count);
            for (int i = 0; i < count; i++) {
                libvlc_player_program_t programInstance = LibVlc.libvlc_player_programlist_at(programList, new size_t(i));
                // This native instance must NOT be freed here
                result.add(new Program(programInstance));
            }
            LibVlc.libvlc_player_programlist_delete(programList);
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public void select(int programId) {
        LibVlc.libvlc_media_player_select_program_id(mediaPlayerInstance, programId);
    }

    public Program selected() {
        libvlc_player_program_t programInstance = LibVlc.libvlc_media_player_get_selected_program(mediaPlayerInstance);
        return convertAndFree(programInstance);
    }

    public Program get(int programId) {
        libvlc_player_program_t programInstance = LibVlc.libvlc_media_player_get_program_from_id(mediaPlayerInstance, programId);
        return convertAndFree(programInstance);
    }

    private static Program convertAndFree(libvlc_player_program_t programInstance) {
        if (programInstance != null) {
            Program program = new Program(programInstance);
            LibVlc.libvlc_player_program_delete(programInstance);
            return program;
        } else {
            return null;
        }
    }
}
