# TODO: FIRST STEPS
- Add to gradle all required libraries ``Status: Done ✅``
  - https://github.com/sealedtx/java-youtube-downloader
  - https://github.com/walkyst/lavaplayer-fork
  - https://github.com/aikaterna/lavaplayer-natives
  - https://github.com/CreativeMD/vlcj
  - https://github.com/CreativeMD/vlcj-natives
  - https://github.com/mpatric/mp3agic
- Shadow/Relocate package of dependencies (me.srrapero720.<library-name>) ``Status: Done ✅``
- Migrate to Parchment mappings``Status: Done ✅``
- Add fabric to gradle ``Status: Done ✅``
- Make classes for each loader ``Status: Done ✅``
- Create a mixin based on these commits (SrRapero720) ``Status: Waiting``
  - https://github.com/CreativeMD/vlcj/commit/0d26cae33082b1783fa4057776a07f9d857037e6
- Wrap required binaries in resources folder ``Status: Waiting``
- make an unzip impl to extract all binaries in ``config/watermedia/bins/<binaries-dir>`` ``Status: Waiting``
- Handle pre-loading state to load API ``Status: Done ✅`` (can be more early-loaded)

# TODO: API STEPS
- API can do async check for native VLC usability to Youtube sources
- API can register any active mediaplayer and attempt to unload "canUnload" player
- VLC 