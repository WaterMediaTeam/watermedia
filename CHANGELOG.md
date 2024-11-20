# 📦 UPDATE 2.1.5
- 🐛 Fixed NPE issues while loading VLC on some OS

# 📦 UPDATE 2.1.4
**NOTE: this contains breaking changes on WaterMedia internals
if you're using the API you have nothing to worry about**
- ✨ Added option to define custom VLC folder (`config/watermedia/custom_vlc_path.txt`)
- ✨ Added back JVM argument `Dwatermedia.userDiscoveryPath=/path/` to define custom VLC folder
- ✨ Detected `application/vnd.apple.mpegurl` and `application/x-mpegurl` as playable video mimetypes (suggested by rlishchyshyn)
- 🛠️ Removed `Dwatermedia.disableMacOsWorkarrounds`
- 🛠️ Discovery now search recursively on folders which contains "VLC" in the name
- 🛠️ Discovery won't be searching after a completed-failed search
- 🛠️ Discovery will continue searching using the current provider when it has more folders instead of skip to the next provider
- 🛠️ Default folder providers are re-priorized from "high/highest" to "normal/low"
- 🛠️ `PlayerAPI#registerFactory` is now synchronized
- 🛠️ Disabled VLC key and mouse input handling

# 📦 UPDATE 2.1.3
- 🐛 Fixed MacOS workarrounds aren't applied when ``Dvideolan4j.disableMacOsWorkarrounds`` argument is not present
- 🛠️ Added minimal VLC bindings for version and instance testing (no more dumb crashes when VLC 4 is installed)

# 📦 UPDATE 2.1.2
- 🐛 Added stub for server-side fabric because fabric still doesn't add sided dependencies but likes breaks things 

# 📦 BREAKING UPDATE 2.1.1
### IMPORTANT: THIS IS (YET AGAIN) A BREAKING UPDATE! DEPENDENT MODS WILL NOT WORK IF THEY DIDN'T RELEASE A COMPATIBILITY UPDATE
**WHY ANOTHER BREAKING UPDATE?**
Release 2.1.0 has some critical stuff to begin addressed, we do a internal breaking change as a critical update
Sorry for all devs already working on v2.1.0, no much changes are done
- 🛠️ Changed the return type of `MathAPI#tickToMs(float): long` to `MathAPI#tickToMs(float): int`
- 🛠️ Removed deprecated variant `MathAPI#tickToMs(int): long`
- 🛠️ Removed deprecated method `WaterMediaAPI#math_colorARGB()`
- ✨ Added `ImageCache` instances for the default resources in `ImageAPI`
- ✨ Added `-Dwatermedia.disableVLC=<true/false>` to prevent VLC begin loaded/extracted by WATERMeDIA
  - Overhaul all other arguments, making `-Dwatermedia.slavist=<true/false>` overrideable
- 🐛 Fixed critical `EXCEPTION_ACCESS_VIOLATION` looping media on VLC
  - Removed VLCJ videosurface argument in replace of a _custom-made_ callback interface for pre-buffers release
- 🐛 Fixed Kick.com support (again)

# 📦 BREAKING UPDATE 2.1.0
### IMPORTANT: THIS UPDATE IS A BREAKING UPDATE! DEPENDENT MODS WILL NOT WORK IF THEY DIDN'T RELEASE A COMPATIBILITY UPDATE
**WHY UPDATE IS SMALL?**<br>
This update is focused on fix critical problems on v2 which requires breaking changes, we didn't put much
effort on this update to entirely focus on v3 development with way many and better features.<br>
Consider get hyped, this update helps me a lot on how to enhance performance on v3 and how to do SoundPhysics (i made OpenAL works)
- ✨ Fixed and enhanced URI handling
  - All usages of `java.net.URL` has been replaced with `java.net.URI`
  - `local://<path>` was replaced with `water://local/<path>`
  - Uris created via `NetworkAPI#createURI()` has direct paths compatibility like `C:\Users\ME\Downloads` (no more `file:///`)
  - Non-HTTP protocols (supported by Java) are now supported (back again)
  - Fixed some URL's capable to be loaded in VLC aren't loaded by WATERMeDIA
- ✨ General enhancing to media loading
  - Rewritten ImageFetch utility
  - Image loading is way faster
  - Enhanced image loading stability and debugging (no more image loading deadlocks)
- ✨ Reduced memory allocation rate while playing videos
- ✨ Lock video buffers to prevent windows (or unix) sore memory on disk
- ✨ Removed unnecessary loading.gif extraction (instead detects if its present to override default)
- ✨ Updated default loading gif (now we use a square gif)
- ✨ Enhanced VLC discovery to be more recursive
- 🛠️ Relocated ``me.srrapero720`` package to `org.watermedia` (part of the v3 refactor)
- 🛠️ Renamed `SyncMediaPlayer` to `MediaPlayer` and all its downclasses
- 🛠️ Renamed `rendering.RenderAPI` to `render.RenderAPI`
- 🛠️ Renamed methods similar to `getUrl()` to `getUri()`
- 🛠️ Removed deprecated methods
- 🛠️ Added JVM argument `-Dwatermedia.slavist=<true/false>` (enables HD videos from YT)