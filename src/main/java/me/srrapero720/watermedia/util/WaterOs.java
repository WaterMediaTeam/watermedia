package me.srrapero720.watermedia.util;

import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;

public enum WaterOs {
    WIN_X64("win", "x64", ".dll", true),
    WIN_ARM64("win", "arm64", ".dll", false),
    MAC_X64("mac", "x64", ".dylib", false),
    MAC_ARM64("mac", "arm64", ".dylib", false),
    NIX_X64("nix", "x64", ".os", false),
    NIX_ARM64("nix", "arm64", ".os", false);

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
        String arch = System.getProperty("os.arch");
        if ((arch.equals("amd64") || arch.equals("x86_64"))) {
            if (RuntimeUtil.isWindows()) return WIN_X64;
            if (RuntimeUtil.isMac()) return MAC_X64;
            if (RuntimeUtil.isNix()) return NIX_X64;
        } else if (arch.equals("arm64")) {
            if (RuntimeUtil.isWindows()) return WIN_ARM64;
            if (RuntimeUtil.isMac()) return MAC_ARM64;
            if (RuntimeUtil.isNix()) return NIX_ARM64;
        }
        throw new RuntimeException("Running Minecraft in a unknown arch");
    }
}
