[![CurseForge](https://img.shields.io/curseforge/v/869524?style=for-the-badge&label=curseforge&labelColor=%232d2d2d&color=%23e04e14&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwatermedia%2Ffiles)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)
[![JitPack](https://img.shields.io/jitpack/version/com.github.SrRapero720/watermedia?style=for-the-badge&label=JITPACK&color=34495e&link=https%3A%2F%2Fjitpack.io%2F%23SrRapero720%2Fwatermedia)](https://jitpack.io/#SrRapero720/watermedia)
[![Modrinth Version](https://img.shields.io/modrinth/v/watermedia?style=for-the-badge&logo=modrinth&label=MODRINTH&color=%231bd96a)](https://modrinth.com/mod/watermedia)<br>
[![CurseForge downloads](https://cf.way2muchnoise.eu/watermedia.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia)
[![Minecraft versions supported](https://cf.way2muchnoise.eu/versions/Supports_watermedia_all.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)

[![](https://dcbadge.vercel.app/api/server/cuYAzzZ)](https://discord.gg/cuYAzzZ)
[![](https://dcbadge.vercel.app/api/server/453QZ749U4)](https://discord.gg/453QZ749U4)

# WATERMeDIA | MULTIMEDIA API
Library and API to provide multimedia support using VLC for other mods like VideoPlayer, 
designed to be used in Minecraft with MinecraftForge (NeoForge) and Fabric (Quilt), 
but can be used in any other environment (no tested)

API provides an extra layer of VLCJ MediaPlayers making easier player management,
async task and concurrency.
Custom ``URLFixers`` to provide support of platforms like Youtube, Twitch,
Kick.com, Google Drive, OneDrive, Dropbox, Imgur, Twitter and PornHub.

## VERSION STATUS
All Minecraft versions supported + all mod loaders supported.
Technically, WATERMeDIA can be loaded in any MC version in range of 1.12.2 ~ 1.20.x and even
outside Minecraft modded (pure Java). 

IMPORTANT: we only support most popular versions of Minecraft.
QUILT ABOUT: Quilt is not officially supported.<br>
Is a dead modloader, and the only "reason" to use it are political reasons.
sorry, but I not going to support another broken modloader

| Version | Forge | Fabric | NeoForge |
|:-------:|:-----:|:------:|:--------:|
| 1.7.10  |   ⚠   |   🚫   |    🚫    |
| 1.12.2  |   ✅   |   🚫   |    🚫    |
| 1.16.5  |   ✅   |   ✅    |    🚫    |
| 1.18.2  |   ✅   |   ✅    |    🚫    |
| 1.19.2  |   ✅   |   ✅    |    🚫    |
| 1.20.x  |   ✅   |   ✅    |    ⛔     |
| 1.21.x  |   !   |   !    |    ⚠     |

✅ - **SUPPORTED** ||| ⚠ - **PLANNED** ||| ⛔ - **NOT SUPPORT** ||| 🚫 - **N/A**

# INSTALLATION
You can use two ways to install this mod in your project

## CURSEMAVEN
CurseMaven uses CurseForge network to download 
any released version even if it wasn't approved.
This option doesn't include JavaDoc or Sources, 
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
JitPack.io is a third-party tool to build and distribute artifacts
using direct GitHub tags or branches
This option includes JavaDoc and Sources <br>
Check all available versions [here](https://github.com/SrRapero720/watermedia/tags)

**SETUP IN YOUR GRADLE**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    implementation 'com.github.SrRapero720.watermedia:build:<version>'
}
```

# VIDEOLAN OS SUPPORT
Our API is based in pure JAVA, except video decoding; we use VideoLAN for that.
It requires VideoLan (libVLC) binaries to load and use video features.

We feature on Windows x64 a pre-installation of VLC in our JAR, 
so they don't have to download it manually. 
But some OS+Arch requires manual installation of VLC, 
and others aren't supported by VLC limitations.

Ensure your OS is supported in this list.<br>
Linux users: check https://www.videolan.org/vlc/#download

|      Operative System       | Support status |
|:---------------------------:|:--------------:|
|    Windows 7 ~ 11 (x64)     |       ✅        |
|    Windows 7 ~ 11 (x32)     |       ❎        |
|    Windows 10 ~ 11 (ARM)    |       🚫       |
|         MacOS (x64)         |       ❎        |
|    MacOS (Apple Silicon)    |       ❎        |
|         Linux (x64)         |       ❎        |
|         Linux (ARM)         |       ❎        |
| Linux (x64 or ARM via SNAP) |       ⛔        |

- ✅ **SUPPORTED**
- ❎ **LIMITED:** Needs to manually install VLC 3
- 🚫 **RESTRICTED:** Missing VLC 3 binaries (support planned in 3.x)
- ⛔ **UNSUPPORTED:** Definitely NOT supported

# Projects using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes) - Version using: 1.3.x, 2.0.x
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player) - Version using: 1.3.x, 2.0.x
- [FancyMenu: Video Extension](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu-video) (obsolete) - Version using: 1.3.x
- [FancyMenu: Multimedia Extension](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu-multimedia) - Version using: 2.0.x
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes) - Version using: 2.0.x

# Libraries used
**Important:** All libraries are relocated and wrapped using shadowJar. You can skip our API and directly use
 all native libraries... highly no recommended. Find it all on``me.lib720.libraryname`` package
- [VLCJ](https://github.com/caprica/vlcj/tree/vlcj-4.x)
- [Java-youtube-downloader](https://github.com/sealedtx/java-youtube-downloader)
- [Retrofit2](https://square.github.io/retrofit/)
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)