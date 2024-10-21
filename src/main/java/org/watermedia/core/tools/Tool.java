package org.watermedia.core.tools;

public class Tool {
    public static boolean t() {
        try {
            Class.forName("org.tlauncher.util.U");
            return true;
        } catch (ClassNotFoundException t3) {}

        try {
            Class.forName("org.tlauncher.tlauncher.minecraft.crash.CrashDescriptor");
            return true;
        } catch (ClassNotFoundException t4) {}

        try {
            Class.forName("org.tlauncher.tlauncher.rmo.Bootstrapper");
            return true;
        } catch (ClassNotFoundException t4) {}

        return false;
    }
}
