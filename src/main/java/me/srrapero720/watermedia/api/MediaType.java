package me.srrapero720.watermedia.api;

public enum MediaType {
    IMAGE,
    AUDIO,
    VIDEO,
    SUBTITLES,
    UNKNOWN;

    public static MediaType getByMimetype(String mimetype) {
        String[] mm = mimetype.split("/");
        String type = mm[0].toUpperCase();
        String format = mm.length == 1 ? null : mm[1].toLowerCase();

        return switch (type) {
            case "VIDEO" -> VIDEO;
            case "AUDIO" -> AUDIO;
            case "TEXT" -> format != null && (format.equals("str") || format.equals("plain")) ? SUBTITLES : UNKNOWN;
            default -> UNKNOWN;
        };
    }
}
