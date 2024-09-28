package me.srrapero720.watermedia.api;

public interface MediaModContext {
    String id();
    String name();
    MediaQuality preferredQuality();

    final class Simple implements MediaModContext {
        private final String id;
        private final String name;
        public Simple(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String id() { return id; }
        @Override public String name() { return name; }
        @Override public MediaQuality preferredQuality() { return MediaQuality.HIGHEST; }
    }

}
