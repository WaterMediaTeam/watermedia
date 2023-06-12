package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.internal.util.WaterUtil;

public enum BinManager {
    // CORES
    libvlc(null),
    libvlccore(null),

    // plugins/aarch64
    libdeinterlace_aarch64_plugin("aarch64"),
    libdeinterlace_sve_plugin("aarch64"),

    // plugins/access
    libfilesystem_plugin("access"),
    libhttp_plugin("access"),
    libhttps_plugin("access"),
    libimem_plugin("access"),

    // plugins/audio_filter
    libequalizer_plugin("audio_filter"),

    // plugins/audio_output
    libadummy_plugin("audio_output"),
    libamem_plugin("audio_output"),
    libwaveout_plugin("audio_output"),
    libdirectsound_plugin("audio_output"),

    // plugins/codec
    liba52_plugin("codec"),
    libadpcm_plugin("codec"),
    libaes3_plugin("codec"),
    libaom_plugin("codec"),
    libaraw_plugin("codec"),
    libaribsub_plugin("codec"),
    libavcodec_plugin("codec"),
    libcc_plugin("codec"),
    libcdg_plugin("codec"),
    libcrystalhd_plugin("codec"),
    libcvdsub_plugin("codec"),
    libd3d11va_plugin("codec"),
    libdav1d_plugin("codec"),
    libdca_plugin("codec"),
    libddummy_plugin("codec"),
    libdmo_plugin("codec"),
    libdvbsub_plugin("codec"),
    libdxva2_plugin("codec"),
    libedummy_plugin("codec"),
    libfaad_plugin("codec"),
    libflac_plugin("codec"),
    libfluidsynth_plugin("codec"),
    libg711_plugin("codec"),
    libjpeg_plugin("codec"),
    libkate_plugin("codec"),
    liblibass_plugin("codec"),
    liblibmpeg2_plugin("codec"),
    liblpcm_plugin("codec"),
    libmft_plugin("codec"),
    libmpg123_plugin("codec"),
    liboggspots_plugin("codec"),
    libopus_plugin("codec"),
    libpng_plugin("codec"),
    libqsv_plugin("codec"),
    librawvideo_plugin("codec"),
    librtp_rawvid_plugin("codec"),
    librtpvideo_plugin("codec"),
    libschroedinger_plugin("codec"),
    libscte18_plugin("codec"),
    libscte27_plugin("codec"),
    libsdl_image_plugin("codec"),
    libspdif_plugin("codec"),
    libspeex_plugin("codec"),
    libspudec_plugin("codec"),
    libstl_plugin("codec"),
    libsubsdec_plugin("codec"),
    libsubstx3g_plugin("codec"),
    libsubsusf_plugin("codec"),
    libsvcdsub_plugin("codec"),
    libt140_plugin("codec"),
    libtextst_plugin("codec"),
    libtheora_plugin("codec"),
    libttml_plugin("codec"),
    libtwolame_plugin("codec"),
    libuleaddvaudio_plugin("codec"),
    libvorbis_plugin("codec"),
    libvpx_plugin("codec"),
    libwebvtt_plugin("codec"),
    libx264_plugin("codec"),
    libx265_plugin("codec"),
    libx26410b_plugin("codec"),
    libzvbi_plugin("codec"),

    // plugins/logger
    libconsole_logger_plugin("logger"),
    libfile_logger_plugin("logger"),

    // plugins/lua
    liblua_plugin("lua"),

    // plugins/misc
    libgnutls_plugin("misc"),

    // plugins/spu
    liblogo_plugin("spu"),
    libmarq_plugin("spu"),

    // plugins/video_chroma
    libswscale_plugin("video_chroma"),

    // plugins/video_filter
    libadjust_plugin("video_filter"),
    libalphamask_plugin("video_filter"),
    libdeinterlace_plugin("video_filter"),
    libfps_plugin("video_filter"),

    // plugins/video_output
    libvmem_plugin("video_output"),
    libvdummy_plugin("video_output");

    public final String pluginFolder;

    BinManager(String pluginDir) { this.pluginFolder = pluginDir; }

    // Function to determine the architecture of the system
    public String getSystemArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch.equals("amd64") || arch.equals("x86_64")) {
            return "win-x64";
//        } else if (arch.equals("arm64")) {
//            return "win-arm64";
        } else {
            return "dummy";
        }
    }

    public String getName() { return name() + ".dll"; }

    public void delete() {
        String relativePath = (pluginFolder == null ? getName() : "plugins/" + pluginFolder + "/" + getName());
        WaterUtil.deleteFrom("cache/vlc/" + relativePath);
    }

    public void extract() {
        String relativePath = (pluginFolder == null ? getName() : "plugins/" + pluginFolder + "/" + getName());
        WaterUtil.extractFrom("/vlc/" + getSystemArchitecture() + "/" + relativePath, "cache/vlc/" + relativePath);
    }
}
