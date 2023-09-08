# UPDATE 2.0.1

## CHANGES
- Added loggers for mod version and missing pre-installation of VLC

## API: CHANGES
- Deprecated BasePlayer (use SyncBasePlayer)

# BREAKING UPDATE 2.0.0
This update breaks dependency mods, only update when all mods depending on it releases a compatibility update

## FEATURES
- Code ported to JAVA 8
- Extended support of old versions of Minecraft (Now supported versions are on MinecraftForge 1.12.2 ~ 1.20.x AND FABRIC 1.16.5 ~ 1.20.x)
- FABRIC support is back (but this time 100%)
- Enhanced Boostrap

## API: FEATURES
- Rewrite of Image API
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
- URLFixers: Added NothingSpecialFixers
  - NSFixers are "nothing special." These are disabled by default in our API and other modders should add the capability to enable it by the end user
  - NSFixers can be enabled on PlayerAPI but is restricted on ImageAPI
  - These fixers add compatibility to pages with a not much acceptance by the people. BE AWARE

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