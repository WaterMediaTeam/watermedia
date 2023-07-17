# WATERMeDIA | MULTIMEDIA API
Centralized Minecraft mod to enhance multimedia management using rendering, parsing and
API tools. Begin a useful tool.

## INSTALLATION
We use CurseMaven (curseforge publishing) to all our releases<br>
checkout what is the latest versiom file ID here: https://www.curseforge.com/minecraft/mc-mods/watermedia

**GRADLE REPOSITORIES**
```gradle
repositories {
    maven {
        url "https://www.cursemaven.com"
        content { includeGroup "curse.maven" }
    }
}
```

**GRADLE DEPENDENCIES**
```gradle
dependencies {
    implementation fg.deobf("curse.maven:watermedia-869524:latest-version-file-id")
}
```

## Mods using WATERMeDIA
- [WATERFrAMES](https://www.curseforge.com/minecraft/mc-mods/waterframes)
- [VideoPlayer](https://www.curseforge.com/minecraft/mc-mods/video-player)
- [FancyMenu: Video Extension](https://legacy.curseforge.com/minecraft/mc-mods/fancymenu-video)
- [LittlePictureFrames](https://www.curseforge.com/minecraft/mc-mods/littleframes)

## Libraries used
**Important:** All libraries are relocated and included using shadowJar. basically if you want to use
native libraries instead WaterMediaAPI you need to search them on ``me.lib720.libraryname`` package
- [Java-youtube-downloader](https://github.com/sealedtx/java-youtube-downloader)
- [Retrofit](https://square.github.io/retrofit/)
- [mp3agic](https://github.com/mpatric/mp3agic)
- [LavaPlayer-Fork](https://github.com/walkyst/lavaplayer-fork)