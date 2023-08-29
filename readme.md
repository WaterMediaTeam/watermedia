[![](https://cf.way2muchnoise.eu/watermedia.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia)
[![](https://cf.way2muchnoise.eu/versions/Supports_watermedia_all.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)
[![](https://img.shields.io/curseforge/v/869524?style=for-the-badge&labelColor=%232d2d2d&color=%23e04e14&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwatermedia%2Ffiles)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)

[![](https://jitpack.io/v/SrRapero720/watermedia.svg?style=flat-square)](https://jitpack.io/#SrRapero720/watermedia)


# WATERMeDIA | MULTIMEDIA API
Library and API to use VideoLAN in Java, specially designed to be used 
in Minecraft with MinecraftForge (NeoForge) and Fabric (Quilt)

API adds a VideoPlayer layer on VLC MediaPlayer to render frames into OpenGL.
Tool to fetch images from the internet and store it in cache,
``URLFixer``'s to give extra support to streaming platforms like Twitch,
Kick.com, YouTube, Google Drive, One Drive, Dropbox and more

# INSTALLATION
You can use two ways to install this mod in your project

## CURSEMAVEN
CurseMaven uses CurseForge network to download any released version even if it wasn't approved
this option doesn't include JavaDoc or Sources,
so all stacktrace doesn't going to match with your decompiled bytecode
checkout what is the latest version file ID here: https://www.curseforge.com/minecraft/mc-mods/watermedia

**SETUP IN YOUR GRADLE**
```gradle
repositories {
    maven {
        url "https://www.cursemaven.com"
        content { includeGroup "curse.maven" }
    }
}

dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    implementation "curse.maven:watermedia-869524:latest-version-file-id"
}
```

## JITPACK.IO
JitPack.io is a third-party tool to build and distribute artifacts using direct github sources/tags
This option includes JavaDoc and Sources

**SETUP IN YOUR GRADLE**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    implementation 'com.github.SrRapero720.watermedia:build:master-SNAPSHOT'
}
```

# OS SUPPORT (VLC)
Any other aspect of our API is supported... except VLC.
We require LibVLC binaries to give video support, 
depending on your OS and your system arch support can be full, limited or unsupported.

| Operative System | Support status |
|:----------------:|:--------------:|
|   Windows x64    |       ✅        |
|   Windows x32    |       ❎        |
|   Windows ARM    |       🚫       |
| MacOS (any Arch) |       ⛔        |
|   Linux (x64)    |       ❎        |
|   Linux (ARM)    |       🚫       |

- ✅ **Supported**
- ❎ **Limited:** Requires manually VLC install
- 🚫 **Unsupported:** limitations (planned support)
- ⛔ **Unsupported:** rejected support by us

# Projects using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes)
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player)
- [FancyMenu: Video Extension](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu-video)
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes)

# Libraries used
**Important:** All libraries are relocated and wrapped using shadowJar. You can skip our API and directly use
 all native libraries... highly no recommended. Find it all on``me.lib720.libraryname`` package
- [VLCJ](https://github.com/caprica/vlcj/tree/vlcj-4.x)
- [Java-youtube-downloader](https://github.com/sealedtx/java-youtube-downloader)
- [Retrofit2](https://square.github.io/retrofit/)
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)