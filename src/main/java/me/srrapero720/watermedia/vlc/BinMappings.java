package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.MediaUtil;

import java.nio.file.Path;

public enum BinMappings {
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

    // plugins/demux
    libadaptive_plugin("demux"),
    libaiff_plugin("demux"),
    libasf_plugin("demux"),
    libau_plugin("demux"),
    libavi_plugin("demux"),
    libcaf_plugin("demux"),
    libdemux_cdg_plugin("demux"),
    libdemux_chromecast_plugin("demux"),
    libdemux_stl_plugin("demux"),
    libdemuxdump_plugin("demux"),
    libdiracsys_plugin("demux"),
    libdirectory_demux_plugin("demux"),
    libes_plugin("demux"),
    libflacsys_plugin("demux"),
    libgme_plugin("demux"),
    libh26x_plugin("demux"),
    libimage_plugin("demux"),
    libmjpeg_plugin("demux"),
    libmkv_plugin("demux"),
    libmod_plugin("demux"),
    libmp4_plugin("demux"),
    libmpc_plugin("demux"),
    libmpgv_plugin("demux"),
    libnoseek_plugin("demux"),
    libnsc_plugin("demux"),
    libnsv_plugin("demux"),
    libnuv_plugin("demux"),
    libogg_plugin("demux"),
    libplaylist_plugin("demux"),
    libps_plugin("demux"),
    libpva_plugin("demux"),
    librawaud_plugin("demux"),
    librawdv_plugin("demux"),
    librawvid_plugin("demux"),
    libreal_plugin("demux"),
    libsid_plugin("demux"),
    libsmf_plugin("demux"),
    libsubtitle_plugin("demux"),
    libts_plugin("demux"),
    libtta_plugin("demux"),
    libty_plugin("demux"),
    libvc1_plugin("demux"),
    libvobsub_plugin("demux"),
    libvoc_plugin("demux"),
    libwav_plugin("demux"),
    libxa_plugin("demux"),

    // plugins/logger
    libconsole_logger_plugin("logger"),
    libfile_logger_plugin("logger"),

    // plugins/lua
    liblua_plugin("lua"),

    // plugins/misc
    libgnutls_plugin("misc"),

    // plugins/mux
    libmux_asf_plugin("mux"),
    libmux_avi_plugin("mux"),
    libmux_dummy_plugin("mux"),
    libmux_mp4_plugin("mux"),
    libmux_mpjpeg_plugin("mux"),
    libmux_ogg_plugin("mux"),
    libmux_ps_plugin("mux"),
    libmux_ts_plugin("mux"),
    libmux_wav_plugin("mux"),

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
    BinMappings(String pluginDir) { this.pluginFolder = pluginDir; }

    public String getName() { return name() + ".dll"; }

    public void delete(Path from) {
        String relativePath = (pluginFolder == null ? getName() : "plugins/" + pluginFolder + "/" + getName());
        MediaUtil.deleteFrom(from.toAbsolutePath() + relativePath);
    }

    public void extract(Path to) {
        String relativePath = (pluginFolder == null ? getName() : "plugins/" + pluginFolder + "/" + getName());
        MediaUtil.extractFrom("/vlc/" + MediaUtil.getOsArch() + "/" + relativePath, to.toAbsolutePath() + relativePath);
    }
}
