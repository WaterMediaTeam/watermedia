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

package org.watermedia.videolan4j.player.embedded.videosurface.callback;

import java.nio.ByteBuffer;

/**
 * Default implementation for a {@link BufferFormatCallback}.
 * <p>
 * This component simply provides an empty implementation for {@link #allocatedBuffers(ByteBuffer[])} since it is not
 * required in all cases.
 * @deprecated Deprecated by WATERMeDIA.<br>
 * Use instead {@link SimpleBufferFormatCallback}
 * This class is keep it just for internal usages
 */
@Deprecated // WATERMeDIA PATCH
public abstract class BufferFormatCallbackAdapter implements BufferFormatCallback {

    @Override
    public void allocatedBuffers(ByteBuffer[] buffers) {
    }
}