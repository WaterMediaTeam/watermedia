[![CurseForge](https://img.shields.io/curseforge/v/869524?style=for-the-badge&label=curseforge&labelColor=%232d2d2d&color=%23e04e14&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwatermedia%2Ffiles)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)
[![JitPack](https://img.shields.io/jitpack/version/com.github.SrRapero720/watermedia?style=for-the-badge&label=JITPACK&color=34495e&link=https%3A%2F%2Fjitpack.io%2F%23SrRapero720%2Fwatermedia)](https://jitpack.io/#SrRapero720/watermedia)
[![Modrinth Version](https://img.shields.io/modrinth/v/watermedia?style=for-the-badge&logo=modrinth&label=MODRINTH&color=%231bd96a)](https://modrinth.com/mod/watermedia)<br>
[![CurseForge downloads](https://cf.way2muchnoise.eu/watermedia.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia)
[![Minecraft versions supported](https://cf.way2muchnoise.eu/versions/Supports_watermedia_all.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)

[![](https://dcbadge.vercel.app/api/server/cuYAzzZ)](https://discord.gg/cuYAzzZ)
[![](https://dcbadge.vercel.app/api/server/453QZ749U4)](https://discord.gg/453QZ749U4)

# WATERMeDIA | MULTIMEDIA API
API-Library providing multimedia support using LibVLC, used by Minecraft mods like VideoPlayer.
Designed to be used in pure Java and Minecraft environments using MinecraftForge (NeoForge) and Fabric (Quilt).

our API provides an extra layer of VLCJ MediaPlayers making easier player management,
async task and concurrency.
Custom ``URLFixers`` to provide support of platforms like YouTube, Twitch,
Kick.com, Google Drive, OneDrive, Dropbox, Imgur, Twitter and PornHub.

## VERSION STATUS
WATERMeDIA can be loaded in any MC version in range of 1.16.5 ~ 1.21.x and
outside Minecraft adding the proper dependencies.

> [!IMPORTANT]
> We only support the most popular versions of Minecraft.

| Version | Forge | Fabric | NeoForge |
|:-------:|:-----:|:------:|:--------:|
| 1.16.5  |  🚨   |   🚨   |    🚫    |
| 1.18.2  |   ✅   |   ✅    |    🚫    |
| 1.19.2  |   ✅   |   ✅    |    🚫    |
| 1.20.1  |   ✅   |   ✅    |    ✅     |
| 1.20.x  |   ✅   |   ✅    |    ⛔     |
| 1.21.1  |   ✅   |   ✅    |    ✅     |

✅ - **SUPPORTED** ||| ⚠ - **PLANNED** ||| ⛔ - **NOT SUPPORT** ||| 🚫 - **N/A** ||| 🚨 - **DROPPING**

# INSTALLATION
We use JitPack.io to build and distribute artifacts using direct GitHub tags or branches.
<br>Check all available versions here: https://github.com/WaterMediaTeam/watermedia/tags

**IN YOUR GRADLE**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    // watermedia is minecraft-agnostic (we don't believe in notch, or agnes)
    implementation 'com.github.WaterMediaTeam.watermedia:build:<version>'
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

> [!WARNING]
> FLATPAK is not supported (or any archlinux thing), use apt-get or manual installation for VLC and Minecraft (or any game)

> [!WARNING]
> MacOS should install universal arch version (avoiding arch concerns)
> Download the universal version [here](https://get.videolan.org/vlc/3.0.21/macosx/vlc-3.0.21-universal.dmg)

| Operative System | Support status |
|:----------------:|:--------------:|
|  Win  1x (x64)   |       ✅        |
|  Win  1x (x32)   |       ⛔        |
|  Win  1x (ARM)   |       🚫       |
|   MacOS (x64)    |       ❎        |
|   MacOS (ARM)    |       ❎        |
|   Linux (x64)    |       ❎        |
|   Linux (ARM)    |       ❎        |
| Android (Pojav)  |       ⚠        |

- ✅ **SUPPORTED**
- ❎ **LIMITED:** Needs manually VLC 3 installation
- ⚠ **PLANNED:** Requires some research
- 🚫 **NOT COMPATIBLE:** Due to VLC limitations
- ⛔ **UNSUPPORTED:** Not even planned

# Projects using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes) - By SrRapero720
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player) - By NGoedix
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes) - By CreativeMD
- [PictureSign](https://www.curseforge.com/minecraft/mc-mods/picturesign) - By Motschen (TeamMidnightDust) 
- [Nightmare Craft](https://www.curseforge.com/minecraft/modpacks/nightmare-craft-chapter-1) By divinegaminginc
- [FancyMenu (Coming Soon)](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu) - By Keksuccino

# MAINTAINERS
- [SrRapero720](https://github.com/SrRapero720) - Donations: https://paypal.me/SrRapero720
- [NGoedix](https://github.com/NGoedix) - Donations: https://paypal.me/ngoedix

# CONTRIBUTORS
- [zenoarrows](https://github.com/ZenoArrows) - Buffer Eater
- [cyyynthia](https://github.com/cyyynthia) - Deadlock hunter

# SHADED DEPENDENCIES
**Important:** All libraries are relocated and wrapped using shadowJar.
- [VLCJ](https://github.com/caprica/vlcj/tree/vlcj-4.x)
- [Java-youtube-downloader](https://github.com/sealedtx/java-youtube-downloader)
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)
- commons-io
- commons-lang3
- commons-codec

# NO SHADED DEPENDENCIES
- LWJGL
- jna
- jna-platform
- gson
- log4j-api
- log4j-core