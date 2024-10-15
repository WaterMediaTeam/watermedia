package org.watermedia.core.network;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;
import org.watermedia.core.WaterMediaCore;
import org.watermedia.core.network.patchs.AbstractPatch;

import java.util.ServiceLoader;

public class NetworkCore extends WaterMediaCore {
    private static final ServiceLoader<AbstractPatch> PATCHES = ServiceLoader.load(AbstractPatch.class);

    public void patch(MediaContext context, MRL media) {

    }
}
