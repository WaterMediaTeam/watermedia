package uk.co.caprica.vlcj.binding.internal;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface ReportSizeChanged extends Callback {
    void reportSizeChanged(Pointer opaque, int width, int height);
}
