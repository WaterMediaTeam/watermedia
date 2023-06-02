package me.srrapero720.watermedia.vlc;

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


    musicbrainz("00_musicbrainz", "meta/art"),
    googleimage("01_googleimage", "meta/art"),
    frenchtv("02_frenchtv", "meta/art"),
    lastfm("03_lastfm", "meta/art"),

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
    jamendo_2("jamendo", "sd"),
    ;


    LuaMappings(String dir) {

    }

    LuaMappings(String filename, String dir) {

    }
}
