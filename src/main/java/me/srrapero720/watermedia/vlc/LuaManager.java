package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.WMUtil;

public enum LuaManager {
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
    jamendo("playlist"),
    koreus("playlist"),
    liveleak("playlist"),
    newgrounds("playlist"),
    rockbox_fm_presets("playlist"),
    soundcloud("playlist"),
    twitch("playlist"),
    vimeo("playlist"),
    vocaroo("playlist"),
    youtube("playlist"),

    icecast("sd"),
    jamendo_2("sd", "jamendo");

    private final String dir;
    private final String file;
    LuaManager(String dir) {
        this.dir = dir;
        this.file = null;
    }
    LuaManager(String dir, String file) {
        this.dir = dir;
        this.file = file;
    }

    public String getName() { return name() + ".luac"; }

    public void delete() {
        WMUtil.deleteFrom("cache/vlc/" + "lua/" + dir + "/" + (file == null ? getName() : file + ".luac"));
    }

    public void extract() {
        String relativePath = "lua/" + dir + "/" + (file == null ? getName() : file + ".luac");
        WMUtil.extractFrom("/vlc/" + relativePath, "cache/vlc/" + relativePath);
    }
}
