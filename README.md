[![CurseForge downloads](https://cf.way2muchnoise.eu/watermedia.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia)
[![CurseForge](https://img.shields.io/curseforge/v/869524?style=for-the-badge&label=curseforge&labelColor=%232d2d2d&color=%23e04e14&link=https%3A%2F%2Fwww.curseforge.com%2Fminecraft%2Fmc-mods%2Fwatermedia%2Ffiles)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)
[![Minecraft versions supported](https://cf.way2muchnoise.eu/versions/Supports_watermedia_all.svg?badge_style=for_the_badge)](https://www.curseforge.com/minecraft/mc-mods/watermedia/files)
[![JitPack](https://img.shields.io/jitpack/version/com.github.SrRapero720/watermedia?style=for-the-badge&label=JITPACK&color=34495e&link=https%3A%2F%2Fjitpack.io%2F%23SrRapero720%2Fwatermedia)](https://jitpack.io/#SrRapero720/watermedia)

[![](https://dcbadge.vercel.app/api/server/cuYAzzZ)](https://discord.gg/cuYAzzZ)
[![](https://dcbadge.vercel.app/api/server/453QZ749U4)](https://discord.gg/453QZ749U4)

# 🔗 WaterMedia: Multimedia API
API and Library, provides multimedia support for pictures and videos using hand-made decoders and LibVLC,
a very extensive API used by Minecraft mods like VideoPlayer, LittleFrames and PictureSign.
Designed to work on Java games like Minecraft via MinecraftForge, NeoForge and Fabric.

Our API is structured to be easy to use for any purpose, audio playing, video playing... catching, downloading.
Enhanced support for many streaming platforms like Youtube, Twitch, Kick, 
Google Drive, OneDrive, Dropbox, Imgur, Twitter, Pornhub and more

## 👷 MAINTAINERS
- [SrRapero720](https://github.com/SrRapero720) | Donations: [Paypal](https://paypal.me/SrRapero720) - [Patreon](https://www.patreon.com/c/SrRapero720) - [Ko-fi](https://ko-fi.com/Manage/Index)
- [NGoedix](https://github.com/NGoedix) | Donations: [Paypal](https://paypal.me/ngoedix)

## 🔢 VERSION STATUS
✅ - **SUPPORTED** ||| ⚠ - **PLANNED** ||| ⛔ - **NOT SUPPORT** ||| 🚫 - **N/A** ||| 🚨 - **DROPPING**

| Version | Forge | Fabric | NeoForge |
|:-------:|:-----:|:------:|:--------:|
| 1.16.5  |  🚨   |   🚨   |    🚫    |
| 1.18.2  |   ✅   |   ✅    |    🚫    |
| 1.19.2  |   ✅   |   ✅    |    🚫    |
| 1.20.1  |   ✅   |   ✅    |    ✅     |
| 1.20.x  |   ✅   |   ✅    |    ⛔     |
| 1.21.1  |   ✅   |   ✅    |    ✅     |
| 1.21.x  |   ⛔   |   ⛔    |    ⛔     |


# 🧑‍💻 INSTALLATION (GRADLE)
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

# ⏯️ VIDEOLAN OS SUPPORT
Windows (x64) users has a pre-installed VLC version. You won't need to install it there (sometimes)
Some OS requires manual installation, others aren't supported.

Ensure your OS is supported in this list.

> [!WARNING]
> Linux: Flatpak and Pak-man is not supported, use `apt-get` instead or any native option.<br>
> MacOS: skip this list and download universal binaries: Download the universal version [here](https://get.videolan.org/vlc/3.0.21/macosx/vlc-3.0.21-universal.dmg).

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

# 👥 Projects using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes) - By SrRapero720
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player) - By NGoedix
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes) - By CreativeMD
- [PictureSign](https://www.curseforge.com/minecraft/mc-mods/picturesign) - By Motschen (TeamMidnightDust) 
- [Nightmare Craft](https://www.curseforge.com/minecraft/modpacks/nightmare-craft-chapter-1) By divinegaminginc
- [FancyMenu (Coming Soon)](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu) - By Keksuccino

# 📦 SHADED DEPENDENCIES
- [Java-Youtube-Downloader](https://github.com/sealedtx/java-youtube-downloader) is public domain
  - [Jackson Databind](https://github.com/FasterXML/jackson) is shaded under [Apache v2.0](https://www.apache.org/licenses/LICENSE-2.0) as a dependency of JYD
- [SevenZipJBinding](https://github.com/borisbrodski/sevenzipjbinding) is shaded under [LGPLv2.1](https://github.com/borisbrodski/sevenzipjbinding?tab=LGPL-2.1-2-ov-file)
- **NO SHADED**: *LWJGL, JNA/JNA-platform, gson, Log4J-api/core*

# ⚖️ LEGAL
## WaterMedia License
Polyform Strict License v1.0.0<br>
Is permitted usage for non-comercial purposes (including and not limited to modpacks, serverpacks).
Redistribution or derivatives works of WaterMedia are not allowed

In case you want to run on commercial purposes, 
you need to contact us to cordinate a comercial license for WaterMedia and VLCJ with [Caprica Software Limited](https://www.capricasoftware.co.uk/)

## VLCJ license
WaterMedia v2.1 shades, and distributes VLCJ and VLCJ-natives
under the [Commercial license for vlcj](https://www.capricasoftware.co.uk/docs/Caprica%20Software%20vlcj%20Commercial%20License%20Standard%20Offer.pdf) 
donated by [Caprica Software Limited](https://www.capricasoftware.co.uk/)

## LibVLC license
LibVLC binaries for Windows 10 (x64) is shaded under [LGPLv2.1](https://code.videolan.org/videolan/vlc/-/blob/master/COPYING)