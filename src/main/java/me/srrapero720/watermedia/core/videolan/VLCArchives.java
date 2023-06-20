package me.srrapero720.watermedia.core.videolan;

import me.srrapero720.watermedia.MediaUtil;
import me.srrapero720.watermedia.api.external.ThreadUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public enum VLCArchives {
    // CORES
    libvlc(ResFileType.BIN, null),
    libvlccore(ResFileType.BIN, null),

//    // plugins/aarch64
//    libdeinterlace_aarch64_plugin("aarch64"),
//    libdeinterlace_sve_plugin("aarch64"),

    // plugins/access
    libfilesystem_plugin(ResFileType.BIN, "access"),
    libhttp_plugin(ResFileType.BIN, "access"),
    libhttps_plugin(ResFileType.BIN, "access"),
    libimem_plugin(ResFileType.BIN, "access"),

    // plugins/audio_filter
    libequalizer_plugin(ResFileType.BIN, "audio_filter"),
    libgain_plugin(ResFileType.BIN, "audio_filter"),
    libscaletempo_pitch_plugin(ResFileType.BIN, "audio_filter"),
    libscaletempo_plugin(ResFileType.BIN, "audio_filter"),

    // plugins/audio_output
    libadummy_plugin(ResFileType.BIN, "audio_output"),
    libamem_plugin(ResFileType.BIN, "audio_output"),
    libdirectsound_plugin(ResFileType.BIN, "audio_output"),
    libwasapi_plugin(ResFileType.BIN, "audio_output"),
    libwaveout_plugin(ResFileType.BIN, "audio_output"),

    // plugins/codec
    liba52_plugin(ResFileType.BIN, "codec"),
    libadpcm_plugin(ResFileType.BIN, "codec"),
    libaes3_plugin(ResFileType.BIN, "codec"),
    libaom_plugin(ResFileType.BIN, "codec"),
    libaraw_plugin(ResFileType.BIN, "codec"),
    libaribsub_plugin(ResFileType.BIN, "codec"),
    libavcodec_plugin(ResFileType.BIN, "codec"),
    libcc_plugin(ResFileType.BIN, "codec"),
    libcdg_plugin(ResFileType.BIN, "codec"),
    libcrystalhd_plugin(ResFileType.BIN, "codec"),
    libcvdsub_plugin(ResFileType.BIN, "codec"),
    libd3d11va_plugin(ResFileType.BIN, "codec"),
    libdav1d_plugin(ResFileType.BIN, "codec"),
    libdca_plugin(ResFileType.BIN, "codec"),
    libddummy_plugin(ResFileType.BIN, "codec"),
    libdmo_plugin(ResFileType.BIN, "codec"),
    libdvbsub_plugin(ResFileType.BIN, "codec"),
    libdxva2_plugin(ResFileType.BIN, "codec"),
    libedummy_plugin(ResFileType.BIN, "codec"),
    libfaad_plugin(ResFileType.BIN, "codec"),
    libflac_plugin(ResFileType.BIN, "codec"),
    libfluidsynth_plugin(ResFileType.BIN, "codec"),
    libg711_plugin(ResFileType.BIN, "codec"),
    libjpeg_plugin(ResFileType.BIN, "codec"),
    libkate_plugin(ResFileType.BIN, "codec"),
    liblibass_plugin(ResFileType.BIN, "codec"),
    liblibmpeg2_plugin(ResFileType.BIN, "codec"),
    liblpcm_plugin(ResFileType.BIN, "codec"),
    libmft_plugin(ResFileType.BIN, "codec"),
    libmpg123_plugin(ResFileType.BIN, "codec"),
    liboggspots_plugin(ResFileType.BIN, "codec"),
    libopus_plugin(ResFileType.BIN, "codec"),
    libpng_plugin(ResFileType.BIN, "codec"),
    libqsv_plugin(ResFileType.BIN, "codec"),
    librawvideo_plugin(ResFileType.BIN, "codec"),
    librtp_rawvid_plugin(ResFileType.BIN, "codec"),
    librtpvideo_plugin(ResFileType.BIN, "codec"),
    libschroedinger_plugin(ResFileType.BIN, "codec"),
    libscte18_plugin(ResFileType.BIN, "codec"),
    libscte27_plugin(ResFileType.BIN, "codec"),
    libsdl_image_plugin(ResFileType.BIN, "codec"),
    libspdif_plugin(ResFileType.BIN, "codec"),
    libspeex_plugin(ResFileType.BIN, "codec"),
    libspudec_plugin(ResFileType.BIN, "codec"),
    libstl_plugin(ResFileType.BIN, "codec"),
    libsubsdec_plugin(ResFileType.BIN, "codec"),
    libsubstx3g_plugin(ResFileType.BIN, "codec"),
    libsubsusf_plugin(ResFileType.BIN, "codec"),
    libsvcdsub_plugin(ResFileType.BIN, "codec"),
    libt140_plugin(ResFileType.BIN, "codec"),
    libtextst_plugin(ResFileType.BIN, "codec"),
    libtheora_plugin(ResFileType.BIN, "codec"),
    libttml_plugin(ResFileType.BIN, "codec"),
    libtwolame_plugin(ResFileType.BIN, "codec"),
    libuleaddvaudio_plugin(ResFileType.BIN, "codec"),
    libvorbis_plugin(ResFileType.BIN, "codec"),
    libvpx_plugin(ResFileType.BIN, "codec"),
    libwebvtt_plugin(ResFileType.BIN, "codec"),
    libx264_plugin(ResFileType.BIN, "codec"),
    libx265_plugin(ResFileType.BIN, "codec"),
    libx26410b_plugin(ResFileType.BIN, "codec"),
    libzvbi_plugin(ResFileType.BIN, "codec"),

    // plugins/demux
    libadaptive_plugin(ResFileType.BIN, "demux"),
    libaiff_plugin(ResFileType.BIN, "demux"),
    libasf_plugin(ResFileType.BIN, "demux"),
    libau_plugin(ResFileType.BIN, "demux"),
    libavi_plugin(ResFileType.BIN, "demux"),
    libcaf_plugin(ResFileType.BIN, "demux"),
    libdemux_cdg_plugin(ResFileType.BIN, "demux"),
    libdemux_chromecast_plugin(ResFileType.BIN, "demux"),
    libdemux_stl_plugin(ResFileType.BIN, "demux"),
    libdemuxdump_plugin(ResFileType.BIN, "demux"),
    libdiracsys_plugin(ResFileType.BIN, "demux"),
    libdirectory_demux_plugin(ResFileType.BIN, "demux"),
    libes_plugin(ResFileType.BIN, "demux"),
    libflacsys_plugin(ResFileType.BIN, "demux"),
    libgme_plugin(ResFileType.BIN, "demux"),
    libh26x_plugin(ResFileType.BIN, "demux"),
    libimage_plugin(ResFileType.BIN, "demux"),
    libmjpeg_plugin(ResFileType.BIN, "demux"),
    libmkv_plugin(ResFileType.BIN, "demux"),
    libmod_plugin(ResFileType.BIN, "demux"),
    libmp4_plugin(ResFileType.BIN, "demux"),
    libmpc_plugin(ResFileType.BIN, "demux"),
    libmpgv_plugin(ResFileType.BIN, "demux"),
    libnoseek_plugin(ResFileType.BIN, "demux"),
    libnsc_plugin(ResFileType.BIN, "demux"),
    libnsv_plugin(ResFileType.BIN, "demux"),
    libnuv_plugin(ResFileType.BIN, "demux"),
    libogg_plugin(ResFileType.BIN, "demux"),
    libplaylist_plugin(ResFileType.BIN, "demux"),
    libps_plugin(ResFileType.BIN, "demux"),
    libpva_plugin(ResFileType.BIN, "demux"),
    librawaud_plugin(ResFileType.BIN, "demux"),
    librawdv_plugin(ResFileType.BIN, "demux"),
    librawvid_plugin(ResFileType.BIN, "demux"),
    libreal_plugin(ResFileType.BIN, "demux"),
    libsid_plugin(ResFileType.BIN, "demux"),
    libsmf_plugin(ResFileType.BIN, "demux"),
    libsubtitle_plugin(ResFileType.BIN, "demux"),
    libts_plugin(ResFileType.BIN, "demux"),
    libtta_plugin(ResFileType.BIN, "demux"),
    libty_plugin(ResFileType.BIN, "demux"),
    libvc1_plugin(ResFileType.BIN, "demux"),
    libvobsub_plugin(ResFileType.BIN, "demux"),
    libvoc_plugin(ResFileType.BIN, "demux"),
    libwav_plugin(ResFileType.BIN, "demux"),
    libxa_plugin(ResFileType.BIN, "demux"),

    // plugins/logger
    libconsole_logger_plugin(ResFileType.BIN, "logger"),
    libfile_logger_plugin(ResFileType.BIN, "logger"),

    // plugins/lua
    liblua_plugin(ResFileType.BIN,"lua"),

    // plugins/misc
    libgnutls_plugin(ResFileType.BIN, "misc"),

    // plugins/mux
    libmux_asf_plugin(ResFileType.BIN, "mux"),
    libmux_avi_plugin(ResFileType.BIN, "mux"),
    libmux_dummy_plugin(ResFileType.BIN, "mux"),
    libmux_mp4_plugin(ResFileType.BIN, "mux"),
    libmux_mpjpeg_plugin(ResFileType.BIN, "mux"),
    libmux_ogg_plugin(ResFileType.BIN, "mux"),
    libmux_ps_plugin(ResFileType.BIN, "mux"),
    libmux_ts_plugin(ResFileType.BIN, "mux"),
    libmux_wav_plugin(ResFileType.BIN, "mux"),

    // plugins/spu
    liblogo_plugin(ResFileType.BIN,"spu"),
    libmarq_plugin(ResFileType.BIN,"spu"),

    // plugins/stream_filter
    libadf_plugin(ResFileType.BIN, "stream_filter"),
    libaribcam_plugin(ResFileType.BIN, "stream_filter"),
    libcache_block_plugin(ResFileType.BIN, "stream_filter"),
    libcache_read_plugin(ResFileType.BIN, "stream_filter"),
    libhds_plugin(ResFileType.BIN, "stream_filter"),
    libinflate_plugin(ResFileType.BIN, "stream_filter"),
    libprefetch_plugin(ResFileType.BIN, "stream_filter"),
    librecord_plugin(ResFileType.BIN, "stream_filter"),
    libskiptags_plugin(ResFileType.BIN, "stream_filter"),

    // plugins/video_chroma
    libswscale_plugin(ResFileType.BIN, "video_chroma"),

    // plugins/video_filter
    libadjust_plugin(ResFileType.BIN, "video_filter"),
    libalphamask_plugin(ResFileType.BIN, "video_filter"),
    libdeinterlace_plugin(ResFileType.BIN, "video_filter"),
    libfps_plugin(ResFileType.BIN, "video_filter"),

    // plugins/video_output
    libvdummy_plugin(ResFileType.BIN, "video_output"),
    libvmem_plugin(ResFileType.BIN, "video_output"),

    /*
        #####################
        #### LUA SCRIPTS ####
        #####################
    */

    // extensions
    VLSub(ResFileType.LUAC, "extensions"),

    // intf
    cli(ResFileType.LUAC, "intf"),
    dummy(ResFileType.LUAC, "intf"),
    dumpmeta(ResFileType.LUAC, "intf"),
    http(ResFileType.LUAC, "intf"),
    luac(ResFileType.LUAC, "intf"),
    telnet(ResFileType.LUAC, "intf"),

    // intf/modules
    host(ResFileType.LUAC, "intf/modules"),
    httprequests(ResFileType.LUAC, "intf/modules"),

    // modules
    common(ResFileType.LUAC, "modules"),
    dkjson(ResFileType.LUAC, "modules"),
    sandbox(ResFileType.LUAC, "modules"),
    simplexml(ResFileType.LUAC, "modules"),

    // playlist
    newgrounds(ResFileType.LUAC, "playlist"),
    soundcloud(ResFileType.LUAC, "playlist"),
    vimeo(ResFileType.LUAC, "playlist"),
    vocaroo(ResFileType.LUAC, "playlist"),
    youtube(ResFileType.LUAC, "playlist"),
    ;

    private final ResFileType type;
    private final String relativeDir;
    private final String filename;

    private static final String version = "20230618-0226"; // Comes from: https://artifacts.videolan.org/vlc-3.0/nightly-win64/
    VLCArchives(ResFileType resFileType, String fileDir) {
        this.type = resFileType;
        this.relativeDir = fileDir;
        this.filename = name() + resFileType.getExtension();
    }

    static String getVersion() { return version; }
    static String getVersion(Path from) { return ThreadUtil.tryAndReturn(defaultVar -> Files.exists(from) ? Files.readString(from) : defaultVar, null); }

    public void extract(Path to) {
        String relativePath = (relativeDir != null ? (type.equals(ResFileType.BIN) ? "plugins/" : "") + relativeDir + "/" : "") + filename;
        MediaUtil.extractFrom(type.getRootDir() + "/" + relativePath, to.toAbsolutePath() + (type.equals(ResFileType.LUAC) ? "/lua/" : "/") + relativePath);
    }

    public void clear(Path from) {
        String relativePath = (relativeDir != null ? (type.equals(ResFileType.BIN) ? "plugins/" : "lua/") + relativeDir + "/" : "") + filename;
        MediaUtil.deleteFrom(from.toAbsolutePath() + "/" + relativePath);
    }

    enum ResFileType {
        LUAC("/vlc/lua", ".luac"),
        BIN("/vlc/" + MediaUtil.getOsArch(), MediaUtil.getOsBinExtension());

        private final String rootDir;
        private final String extension;
        ResFileType(String rootDir, String ext) { this.rootDir = rootDir; this.extension = ext; }

        public String getRootDir() { return rootDir; }
        public String getExtension() { return extension; }
    }
}