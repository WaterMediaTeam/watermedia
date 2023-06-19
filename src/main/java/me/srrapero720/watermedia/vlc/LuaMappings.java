package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.MediaUtil;

import java.nio.file.Path;

@Deprecated()
public enum LuaMappings {
    VLSub("extensions"),

    cli("intf"),
    dummy("intf"),
    dumpmeta("intf"),
    http("intf"),
    luac("intf"),
    telnet("intf"),

    host("intf/modules"),
    httprequests("intf/modules"),


    musicbrainz("meta/art", "00_musicbrainz"),
    googleimage("meta/art", "01_googleimage"),
    frenchtv("meta/art", "02_frenchtv"),
    lastfm("meta/art", "03_lastfm"),

    filename("meta/reader"),

    common("modules"),
    dkjson("modules"),
    sandbox("modules"),
    simplexml("modules"),

    anevia_streams("playlist"),
    anevia_xml("playlist"),
    appletrailers("playlist"),
    bbc_co_uk("playlist"),
    cue("playlist"),
    dailymotion("playlist"),
    koreus("playlist"),
    liveleak("playlist"),
    newgrounds("playlist"),
    rockbox_fm_presets("playlist"),
    soundcloud("playlist"),
    vimeo("playlist"),
    vocaroo("playlist"),
    ;

    private final String dir;
    private final String file;
    LuaMappings(String dir) {
        this.dir = dir;
        this.file = null;
    }
    LuaMappings(String dir, String file) {
        this.dir = dir;
        this.file = file;
    }

    public String getName() { return name() + ".luac"; }

    public void extract(Path to) {
        String relativePath = "lua/" + dir + "/" + (file == null ? getName() : file + ".luac");
        MediaUtil.extractFrom("/vlc/" + relativePath, to.toAbsolutePath() + "/" + relativePath);
    }

    public void delete(Path from) {
        MediaUtil.deleteFrom(from.toAbsolutePath() + "/lua/" + dir + "/" + (file == null ? getName() : file + ".luac"));
    }
}
