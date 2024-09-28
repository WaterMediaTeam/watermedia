package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;

public class StreamablePatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Streamable";
    }

    @Override
    public boolean validate(MediaURI source) {
        return false;
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        return null;
    }
}
