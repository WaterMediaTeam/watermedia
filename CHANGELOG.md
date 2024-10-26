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