# WATERMeDIA | MULTIMEDIA API
Library and API to use VideoLAN in Java, specially designed to be used in Minecraft with MinecraftForge(NeoForge) and Fabric(Quilt)

API adds a VideoPlayer layer on VLC MediaPlayer to render frames into OpenGL, Tool to fetch images from internet and store it in cache, URLFixers to give extra support to streaming platforms like Twitch, Kick.com, Youtube, Google Drive, One Drive, Dropbox and more

# INSTALLATION
You can use 2 ways to install this mod in your project

### CURSEMAVEN
CurseMaven uses CurseForge network to download any released version even if it wasn't approved
this option doesn't include JavaDoc or Sources, so all stacktrace doesn't going to match with your decompiled bytecode
checkout what is the latest version file ID here: https://www.curseforge.com/minecraft/mc-mods/watermedia
**REPOSITORY**
```gradle
repositories {
    maven {
        url "https://www.cursemaven.com"
        content { includeGroup "curse.maven" }
    }
}
```

**DEPENDENCIES**
```gradle
dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    implementation "curse.maven:watermedia-869524:latest-version-file-id"
}
```

### JITPACK.IO
JitPack.io is a third-party tool to build and distribute artifacts using direct github sources/tags
This option includes JavaDoc and Sources
**REPOSITORIES**
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

**DEPENDENCIES**
```gradle
dependencies {
    // doesn't require fg.debof() or any debofuscation tool
    implementation 'com.github.SrRapero720.watermedia:build:master-SNAPSHOT'
}
```

# Other projects using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes)
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player)
- [FancyMenu: Video Extension](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu-video)
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes)

# Libraries used
**Important:** All libraries are relocated and wrapped using shadowJar. You can skip our API and use
directly all native libraries... highly no recommended. Find it all on``me.lib720.libraryname`` package
- [VLCJ](https://github.com/caprica/vlcj/tree/vlcj-4.x)
- [Java-youtube-downloader](https://github.com/sealedtx/java-youtube-downloader)
- [Retrofit](https://square.github.io/retrofit/)
- [Jackson Databind](https://github.com/FasterXML/jackson-databind)