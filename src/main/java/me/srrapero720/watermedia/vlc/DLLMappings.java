package me.srrapero720.watermedia.vlc;

import com.sun.jna.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DLLMappings {
    // CORES
    libvlc(null, Arch.BOTH),
    libvlccore(null, Arch.BOTH),

    // plugins/aarch64
    libdeinterlace_aarch64_plugin("aarch64", Arch.ARM64),
    libdeinterlace_sve_plugin("aarch64", Arch.ARM64),

    // plugins/access
    libfilesystem_plugin("access", Arch.BOTH),
    libhttp_plugin("access", Arch.BOTH),
    libhttps_plugin("access", Arch.BOTH),
    libimem_plugin("access", Arch.BOTH),

    // plugins/audio_filter
    libequalizer_plugin("audio_filter", Arch.BOTH),

    // plugins/audio_output
    libadummy_plugin("audio_output", Arch.BOTH),
    libamem_plugin("audio_output", Arch.BOTH),
    libwaveout_plugin("audio_output", Arch.BOTH),

    // plugins/codec
    liba52_plugin("codec", Arch.BOTH),
    libadpcm_plugin("codec", Arch.BOTH),
    libaes3_plugin("codec", Arch.BOTH),
    libaom_plugin("codec", Arch.BOTH),
    libaraw_plugin("codec", Arch.BOTH),
    libaribcaption_plugin("codec", Arch.BOTH),
    libaribsub_plugin("codec", Arch.BOTH),
    libavcodec_plugin("codec", Arch.BOTH),
    libcc_plugin("codec", Arch.BOTH),
    libcdg_plugin("codec", Arch.BOTH),
    libcvdsub_plugin("codec", Arch.BOTH),
    libd3d11va_plugin("codec", Arch.BOTH),
    libdav1d_plugin("codec", Arch.BOTH),
    libdca_plugin("codec", Arch.BOTH),
    libddummy_plugin("codec", Arch.BOTH),
    libdmo_plugin("codec", Arch.BOTH),
    libdvbsub_plugin("codec", Arch.BOTH),
    libdxva2_plugin("codec", Arch.BOTH),
    libedummy_plugin("codec", Arch.BOTH),
    libfaad_plugin("codec", Arch.BOTH),
    libflac_plugin("codec", Arch.BOTH),
    libfluidsynth_plugin("codec", Arch.BOTH),
    libg711_plugin("codec", Arch.BOTH),
    libjpeg_plugin("codec", Arch.BOTH),
    libkate_plugin("codec", Arch.BOTH),
    liblibass_plugin("codec", Arch.BOTH),
    liblibmpeg2_plugin("codec", Arch.BOTH),
    liblpcm_plugin("codec", Arch.BOTH),
    libmft_plugin("codec", Arch.BOTH),
    libmpg123_plugin("codec", Arch.BOTH),
    liboggspots_plugin("codec", Arch.BOTH),
    libopus_plugin("codec", Arch.BOTH),
    libpng_plugin("codec", Arch.BOTH),
    librawvideo_plugin("codec", Arch.BOTH),
    librtp_rawvid_plugin("codec", Arch.BOTH),
    librtpvideo_plugin("codec", Arch.BOTH),
    libschroedinger_plugin("codec", Arch.BOTH),
    libscte18_plugin("codec", Arch.BOTH),
    libscte27_plugin("codec", Arch.BOTH),
    libspdif_plugin("codec", Arch.BOTH),
    libspeex_plugin("codec", Arch.BOTH),
    libspudec_plugin("codec", Arch.BOTH),
    libstl_plugin("codec", Arch.BOTH),
    libsubsdec_plugin("codec", Arch.BOTH),
    libsubstx3g_plugin("codec", Arch.BOTH),
    libsubsusf_plugin("codec", Arch.BOTH),
    libsvcdsub_plugin("codec", Arch.BOTH),
    libt140_plugin("codec", Arch.BOTH),
    libtextst_plugin("codec", Arch.BOTH),
    libtheora_plugin("codec", Arch.BOTH),
    libttml_plugin("codec", Arch.BOTH),
    libtwolame_plugin("codec", Arch.BOTH),
    libuleaddvaudio_plugin("codec", Arch.BOTH),
    libvorbis_plugin("codec", Arch.BOTH),
    libvpx_plugin("codec", Arch.BOTH),
    libwebvtt_plugin("codec", Arch.BOTH),
    libx264_plugin("codec", Arch.BOTH),
    libx265_plugin("codec", Arch.BOTH),
    libx26410b_plugin("codec", Arch.BOTH),
    libzvbi_plugin("codec", Arch.BOTH),

    // plugins/logger
    libconsole_logger_plugin("logger", Arch.BOTH),
    libfile_logger_plugin("logger", Arch.BOTH),

    // plugins/lua
    liblua_plugin("lua", Arch.BOTH),

    // plugins/misc
    libgnutls_plugin("misc", Arch.BOTH),

    // plugins/spu
    liblogo_plugin("spu", Arch.BOTH),
    libmarq_plugin("spu", Arch.BOTH),

    // plugins/video_chroma
    libswscale_plugin("video_chroma", Arch.BOTH),

    // plugins/video_filter
    libadjust_plugin("video_filter", Arch.BOTH),
    libalphamask_plugin("video_filter", Arch.BOTH),
    libdeinterlace_plugin("video_filter", Arch.BOTH),
    libfps_plugin("video_filter", Arch.BOTH),

    // plugins/video_output
    libvmem_plugin("video_output", Arch.BOTH),
    libwdummy_plugin("video_output", Arch.BOTH),
    libwin32_window_plugin("video_output", Arch.BOTH);



    DLLMappings(@Nullable String pluginDir, Arch arch) {


    }

    public boolean extract() {
        return false;
    }


    enum Arch {
        ARM64, X64, BOTH, UNSUPPORTED;
        private static Arch arch;
        public static Arch platformArch() {
            if (arch == null) {
                switch (Platform.ARCH) {
                    case ("amd64"), ("x86-64") -> arch = X64;
                    case ("arm64") -> arch = ARM64;
                    default -> arch = UNSUPPORTED;
                }
            }
            return arch;
        }
    }

}
