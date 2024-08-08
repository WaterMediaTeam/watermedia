package uk.co.caprica.vlcj.support.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulation of version information and related behaviors.
 * <p>
 * This may be useful to implement version-specific features.
 * <p>
 * This implementation is not exhaustive, but is good enough for the known vlc
 * versions.
 */
public final class Version implements Comparable<Version> {

    /**
     * Regular expression for matching versions strings.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)[\\-_\\s]?(.*)");

    /**
     * Raw version information.
     */
    private final String version;

    /**
     * Major version number.
     */
    private final int major;

    /**
     * Minor version number.
     */
    private final int minor;

    /**
     * Revision number.
     */
    private final int revision;

    /**
     * Extra.
     */
    private final String extra;

    /**
     * Create a new version.
     *
     * @param version version string
     */
    public Version(final String version) {
        this.version = version;
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            this.major = Integer.parseInt(matcher.group(1));
            this.minor = Integer.parseInt(matcher.group(2));
            this.revision = Integer.parseInt(matcher.group(3));
            if (matcher.groupCount() > 3) {
                this.extra = matcher.group(4);
            } else {
                this.extra = null;
            }
        } else {
            throw new IllegalArgumentException("Can't parse version from '" + version + "'");
        }
    }

    /**
     * Get the original version string.
     *
     * @return version
     */
    public String version() {
        return version;
    }

    /**
     * Get the major version.
     *
     * @return major version number
     */
    public int major() {
        return major;
    }

    /**
     * Get the minor version.
     *
     * @return minor version number
     */
    public int minor() {
        return minor;
    }

    /**
     * Get the revision.
     *
     * @return revision number
     */
    public int revision() {
        return revision;
    }

    /**
     * Get the extra.
     *
     * @return extra
     */
    public String extra() {
        return extra;
    }

    /**
     * Test whether this version is at least the required version.
     *
     * @param required required version
     * @return <code>true</code> if this version is at least (equal to or greater than) the required version
     */
    public boolean atLeast(Version required) {
        return compareTo(required) >= 0;
    }

    @Override
    public int compareTo(Version o) {
        int delta = major - o.major;
        if (delta == 0) {
            delta = minor - o.minor;
            if (delta == 0) {
                delta = revision - o.revision;
                // We don't care about "extra" - it is a legacy artifact that is no longer used in LibVLC versions
            }
        }
        return delta;
    }

    @Override
    public String toString() {
        return version;
    }

}
