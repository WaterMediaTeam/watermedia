package me.srrapero720.watermedia.vlc.extractor;

import org.stringtemplate.v4.STErrorListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public enum LuaExtractor {
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

    private final String dir;
    private final String file;
    LuaExtractor(String dir) {
        this.dir = dir;
        this.file = null;
    }
    LuaExtractor(String file, String dir) {
        this.dir = dir;
        this.file = file;
    }

    public String getName() { return name() + ".luac"; }

    public void extract() {
        String relativePath = "lua/" + dir + "/" + (file == null ? getName() : file + ".luac");
        String destinationPath = "cache/vlc/" + relativePath;
        String originPath = "/vlc/" + relativePath;

        try {
            Path dllDestinationPath = Paths.get(destinationPath);
            if (!Files.exists(dllDestinationPath)) {
                try (var is = getClass().getResourceAsStream(originPath)) {
                    if (is != null) {
                        Files.createDirectories(dllDestinationPath.getParent());
                        Files.copy(is, dllDestinationPath);
                    } else {
                        LOGGER.error("Resource not found: {}", originPath);
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Failed to extract LUAC, file not found: {}" + originPath, fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Failed to extract LUAC due to I/O error: {}" + originPath, ioe);
        }
    }
}
