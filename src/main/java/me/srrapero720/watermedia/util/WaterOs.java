package me.srrapero720.watermedia.util;

import com.sun.jna.Platform;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;

public enum WaterOs {
    WIN_X32("win", "x32", ".dll", false),
    WIN_X64("win", "x64", ".dll", true),
    MAC_X64("mac", "x64", ".dylib", false),
    NIX_X64("nix", "x64", ".os", false),

    WIN_ARM64("win", "arm64", ".dll", false),
    MAC_ARM64("mac", "arm64", ".dylib", false),
    NIX_ARM64("nix", "arm64", ".os", false),

    WIN_ARM("win", "arm", ".dll", false),
    MAC_ARM("mac", "arm", ".dylib", false),
    NIX_ARM("nix", "arm", ".os", false),

    DUMMY("dummy", "dummy", ".dummy", false);

    public final String os, arch, ext;
    public final boolean wrapped;

    WaterOs(String os, String arch, String ext, boolean wrapped) {
        this.os = os;
        this.arch = arch;
        this.ext = ext;
        this.wrapped = wrapped;
    }

    @Override
    public String toString() { return os + "-" + arch; }

    private static final WaterOs ARCH = WaterOs.getArch();
    public static WaterOs getArch() {
        if (ARCH != null) return ARCH;
        String arch = Platform.ARCH;
        switch (arch) {
            case "x86-64":
            case "amd64":
                if (RuntimeUtil.isWindows()) return WIN_X64;
                if (RuntimeUtil.isMac()) return MAC_X64;
                if (RuntimeUtil.isNix()) return NIX_X64;
            case "arm64":
                if (RuntimeUtil.isWindows()) return WIN_ARM64;
                if (RuntimeUtil.isMac()) return MAC_ARM64;
                if (RuntimeUtil.isNix()) return NIX_ARM64;
            case "armel":
            case "arm":
                if (RuntimeUtil.isWindows()) return WIN_ARM;
                if (RuntimeUtil.isMac()) return MAC_ARM;
                if (RuntimeUtil.isNix()) return NIX_ARM;
            case "x86":
                if (RuntimeUtil.isWindows()) return WIN_X32;
                throw new IllegalStateException("Detected x86 but begin non windows");
            default:
                return DUMMY;
        }
    }
}
