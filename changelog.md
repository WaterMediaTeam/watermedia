# UPDATE 2.0.26
- Added ``-Dwatermedia.disableBoot=<boolean>`` argument
  - When enabled, Disables boot completely, useful for not boot in on DataGen
- Removed mixins

# UPDATE 2.0.25
- Added ease mathematical methods to animate positioning
- Added scaleTempo methods to calculate in range time scale
- Rid off DirectSound outside windows
- Disabled cache on ImageAPI fetch

# UPDATE 2.0.24
- Fixed wrong DisplayTest, causing WATERMeDIA to be required on server by FORGE

# UPDATE 2.0.23 (ARCHIVED)
- Removed DisplayTest on 1.18.2
- Removed server-only pair system of DisplayTest (fixes crashes on 1.16.5)

# UPDATE 2.0.22 (ARCHIVED)
- Fixed crash on 1.16.5

# UPDATE 2.0.21 (ARCHIVED)
- Fixed pictures are infinite loading
  - For some reason, IOUtils soft-crashes ImageFetch without throwing any error, it just
stops working.
  - IOUtils was replaced with our byte reader. doing that fixes loading, idk why it happens but yeah finally a freaking fix

# UPDATE 2.0.20 (ARCHIVED)
- Reverted all changes related through UrlAPI
  - This fixes all issues on WATERFrAMES and VideoPlayer with loading local/online resources
  - Mods needs to do some special handling arround ``file:///path/to/file.mp4`` until a real solution was implemented on UrlAPI
- Addressed ``file:///`` protocol on PlayerAPI (VLC wasn't able to understand it)

# UPDATE 2.0.12 (ARCHIVED)
- Fixed: crash on Linux by JPanel natives (again)
- Fixed: Online files aren't loaded by ImageFetch

# UPDATE 2.0.11 (ARCHIVED)
**IMPORTANT ADVICE:** This version contains a lot of potential breaking changes, if mod or any dependent mod
crashes consider downgrading to 2.0.10 (and report it quickly to the authors of the mod)

- Fixed: UrlAPI cannot load local file sources (workaround)
- Fixed: crash on Linux by JPanel
- Fixed: Bootstrap was unintentional ASYNC
  - If you notice sometimes first booting is buggy or broken states of the API; well that isn't normal
  - By accident, all bootstrap methods become ASYNC; that means off-thread, that means a lot of bugs
  - This update may reduce booting times for the first time, after that booting times are "exact same"
- Feature: Reduced build size by 50%
  - Compressed picture resources by ~60%
  - Compressed pre-installed binaries to ~40%
- RenderAPI: Added methods to create and resize DirectByteBuffers
  - SyncVideoPlayer now internally uses a ByteBuffer instead of a ported IntBuffer
- Feature: Boostrap on 1.12.2 is now ASYNC (leading in a fast loading times)

# UPDATE 2.0.10 (ARCHIVED)
- Fixed: buildJar doesn't include VLC binaries.
- Updated VLC failed pictures with a brand-new gif

# UPDATE 2.0.9 ([ARCHIVED](https://discord.com/channels/256109634769780737/1164746686019670097))
- Fixed VLC is not loaded on ArchLinux using pacman (added /bin/ directory)
- Fixed sourceJar includes a copy of VLC binaries (only built jar contains it)
- Fixed warnings on old versions attempting to load environment first time
- Fixed RusticLoader doesn't show what MC version is (broken mocked)
- Improved loggers on NativeDiscovery
- PlayerAPI: deprecated SyncVideoPlayer#preRender() and SyncVideoPlayer#getTexture()
- UrlAPI: added support for file paths

# UPDATE 2.0.8 (ARCHIVED)
- Feature: added tickToMs variant for Float (partial ticks)

# UPDATE 2.0.7 (ARCHIVED)
NOTE: Support for NeoForge 1.20.2 is not planned until they got into a more stable build
- Fixed [#26](https://github.com/SrRapero720/watermedia/issues/23): Crash on startup trying to get OS (from archived version 2.0.6)
- Fixed: new instances of MediaPlayerFactory doesn't log from where it is loaded
- Breaking Change: Renamed URLApi to UrlAPI
- Change: Deprecated entire WaterMediaAPI class (all replacements are documented in class methods)
- Change: Deprecated SyncMediaPlayer constructors
- Change: Enhanced JavaDoc for IEnvLoader
- Change: Reduced Thread priority to SyncBasePlayer#start()
- Feature: RenderAPI - Provides a cross-version rendering tool for OpenGL (from WaterMediaAPI) ``[EXPERIMENTAL]``
  - Includes a basic MemoryTracker for ByteBuffers
  - Deprecates BufferHelper argument on SyncVideoPlayer
- Feature: MathAPI - Provides shortcuts for any special calculation (from WaterMediaAPI)
- Feature: PlayerAPI - Provides all tools for MediaPlayer management (from WaterMediaAPI)
- Feature: isMute() added to SyncBasePlayer
- Feature: added URLFixer for Imgur.com. Now supports Imgur galleries and tag galleries using
browser url

# UPDATE 2.0.6 - ([ARCHIVED](https://github.com/SrRapero720/watermedia/issues/23))
- Fixed "missing vlc binaries" logger message is always thrown even if binaries are included
- Fixed "cannot create directories" for custom loading gif API

# UPDATE 2.0.5 (ARCHIVED)
- Fixed NPE trying to access to url var in some dependent mods

# UPDATE 2.0.4 (ARCHIVED)
- API: Mitosis on ImageAPI#imageRenderer() and renamed to renderer()
- Reduced MediaPlayer#release priority
- Renamed MediaPlayer#enableSpecialFixer()
- Fixed audio resets to 100% after loop
- Removed IMediaLoader#classLoader() from bootstrap
- Deprecated WaterMediaAPI#url_isValid()

# UPDATE 2.0.3 (ARCHIVED)
- Fixed NPE when url is null or empty
- Reduced arguments for VLC and make console output quiet

# UPDATE 2.0.2 (ARCHIVED)
- Added loggers for mod version and missing pre-installation of VLC
- Reduced async priority of image loading (slow loading, more fps)
- Fixed some stun lag loading images if url is null or empty
- Fixed Audio tracks get cut a few seconds before reaching the track end
- API: Deprecated BasePlayer (use SyncBasePlayer)
- API: Downgrade and re-patched VLCJ library to 4.7.x
- API: Deprecated IMediaLoader#classLoader() method
- API: Deprecated WaterMediaAPI#url_registerFixer()
- API: Deprecated entire ImageAPI inside WaterMediaAPI
- API: Deprecated entire URLApi inside WaterMediaAPI
- API: Fixed custom loading gif API is broken in deprecated API
- API: Now can be released ImageCache (please don't) even if it wasn't ready
- API: Added SyncMusicPlayer

# UPDATE 2.0.1 (ARCHIVED)
- Removed mod features (now can start on forge 1.19+)

# BREAKING UPDATE 2.0.0 
This update breaks dependency mods, 
only update when all mods depending on it releases a compatibility update

## FEATURES
- Code ported to JAVA 8
- Extended support of old versions of Minecraft (Now supported versions are on MinecraftForge 1.12.2 ~ 1.20.x AND FABRIC 1.16.5 ~ 1.20.x)
- FABRIC support is back (but this time 100%)
- Enhanced Bootstrap

## API: FEATURES
- Rewrite of ImageAPI
  - ImageCache: friendly cache system for already loaded pictures.
  - ImageFetch: Async class to fetch pictures from internet. now uses ExecutorServices to avoid any kind of sync bug
  - ImageRenderer: container of a picture, can be a gif or just a picture.
- Rewrite of PlayerAPI
    - BasePlayer: VLC Player base. all methods can be used in other threads (do not expect to be 100% stable)
    - VideoPlayer: Extends BasePlayer; includes GL rendering tools to process IntBuffers
    - SyncBasePlayer: A synchro base. all methods should be executed on the instance thread or player thread. Can be executed async task using "submit()"
    - SyncVideoPlayer: Extends SyncBasePlayer; includes GL rendering tools to process IntBuffers
- WaterMediaAPI: Renamed all methods. Now all starts with category_action(arguments)
- Removed special VLC failed pictures for Windows users
- URLApi: Added Special Fixers
  - Special Fixers are "nothing special." These are disabled by default in our API, and other modders should add the capability to enable it by the end user
  - Special Fixers can be enabled on PlayerAPI but are restricted on ImageAPI
  - These fixers add compatibility to pages with not much acceptance by the people. BE AWARE

## CHANGES
- Added more VLC binaries
  - This fixes issues with OGG and MP3 compatibility
  
## BUG FIXES
- Fixed URLFixer for Twitch, TwitterX.
- Fixed logger doesn't gzip last log session file of VLC on Bootstrap
  - Check your tmp directory (watermedia/logs/latest.log) and delete it
  - The Current record is 2GB
  - F for QSMP members

## DEVELOPMENT CHANGES
- Dropped Non-LTS versions
  - LTS versions (for us) are versions with a big player base (1.12.2, 1.16.5, 1.18.2, 1.19.2, 1.20.x)
  - Technically API can be loaded in ANY version of Minecraft (in range of 1.12.2~1.20.x)
  - This was decided just to prevent dependent modders being bugged with "why it doesn't exist for 1.17.x ". They can just blame us :)

## OTHER CHANGES
I made more stuff internally, but I forgot what features were added who added and (whatever)
just enjoy the rewrite