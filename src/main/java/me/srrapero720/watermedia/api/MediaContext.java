package me.srrapero720.watermedia.api;

public interface MediaContext {

    /**
     * Provides the context unique identifier (like a mod id)
     * This helps on debugging to find who do the wrong call
     * @return a unique identifier in range of [a-z][1-9]
     */
    String id();

    /**
     * Elegant name for the logger and crash report handlers, it can be not unique
     * @return a fancy string with the name of your project
     */
    String name();

    /**
     * Preference is established for lower qualities when the exact wanted quality is missing
     * @return true if it should prefer lower qualities, false otherwise
     */
    boolean preferLowerQuality();

    /**
     * Very quick and simple implementation of a MediaModContext, no answers, no questions, just a context
     */
    final class Simple implements MediaContext {
        private final String id;
        private final String name;
        public Simple(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String id() { return id; }
        @Override public String name() { return name; }
        @Override public boolean preferLowerQuality() { return false; }
    }

    final class Static {
        private final String id;
        private final String name;
        private final boolean prefferLowerQuality;
        public Static(String id, String name, boolean preferLowerQuality) {
            this.id = id;
            this.name = name;
            this.prefferLowerQuality = preferLowerQuality;
        }
    }
}
