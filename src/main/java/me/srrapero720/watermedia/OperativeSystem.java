package me.srrapero720.watermedia;

import com.sun.jna.Platform;
import me.srrapero720.watermedia.core.exceptions.UnsupportedArchitechtureException;

/**
 * Contains the current OS, architecture and the binary-wrap state
 * @deprecated see <a href="https://github.com/WaterMediaTeam/watermedia/issues/85">#85</a>
 */
@Deprecated
public enum OperativeSystem {
    WIN_X64("win", "x64", true, false),
    MAC_X64("mac", "x64", true, true),
    NIX_X64("nix", "x64", false, false),
    WIN_ARM64("win", "arm64", false, false),
    MAC_ARM64("mac", "arm64", true, true),
    NIX_ARM64("nix", "arm64", false, false),
    WIN_ARM("win", "arm", false, false),
    MAC_ARM("mac", "arm", true, true),
    NIX_ARM("nix", "arm", false, false),

    DUMMY("dummy", "dummy", false, false);

    private final String name, arch;
    private final boolean wrapped, merged;
    OperativeSystem(String name, String arch, boolean wrapped, boolean merged) {
        this.name = name;
        this.arch = arch;
        this.wrapped = wrapped;
        this.merged = merged;
    }

    @Override
    public String toString() { return name + "-" + arch; }

    // STATIC
    public static final OperativeSystem OS;

    static {
        if (Platform.is64Bit()) {
            if (Platform.isARM()) {
                if (Platform.isWindows()) OS = WIN_ARM64;
                else if (Platform.isMac()) OS = MAC_ARM64;
                else if (Platform.isLinux()) OS = NIX_ARM64;
                else OS = DUMMY;
            } else {
                if (Platform.isWindows()) OS = WIN_X64;
                else if (Platform.isMac()) OS = MAC_X64;
                else if (Platform.isLinux()) OS = NIX_X64;
                else OS = DUMMY;
            }
        } else {
            throw new UnsupportedArchitechtureException();
        }
    }

    public static String getFile() { return OS.wrapped ? (OS.merged ? OS.name + "-all" : OS.name + "-" + OS.arch) + ".7z" : null; }
}