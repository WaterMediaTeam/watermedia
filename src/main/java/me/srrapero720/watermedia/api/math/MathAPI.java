package me.srrapero720.watermedia.api.math;

public class MathAPI {

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Tick count
     * @return ticks converted to MS
     */
    public static long tickToMs(int ticks) { return ticks * 50L; }
    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int msToTick(long ms) { return (int) (ms / 50); }

    /**
     * Returns the floor modulus of the {@code long} arguments.
     * <p>
     * The floor modulus is {@code r = x - (floorDiv(x, y) * y)},
     * has the same sign as the divisor {@code y} or is zero, and
     * is in the range of {@code -abs(y) < r < +abs(y)}.
     *
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such that:
     * <ul>
     *   <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}</li>
     * </ul>
     * <p>
     *
     *     Method doesn't throw exceptions when X and Y is ZERO.
     *     Instead, returns ZERO by default
     *
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @since 2.0.7
     */
    public static long floorMod(long x, long y) {
        try {
            final long r = x % y;
            // if the signs are different and modulo not zero, adjust result
            if ((x ^ y) < 0 && r != 0) {
                return r + y;
            }
            return r;
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public static int floorMod(int x, int y) {
        try {
            final int r = x % y;
            // if the signs are different and modulo not zero, adjust result
            if ((x ^ y) < 0 && r != 0) {
                return r + y;
            }
            return r;
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public static int floorMod(long x, int y) {
        // Result cannot overflow the range of int.
        return (int)floorMod(x, (long)y);
    }

    /**
     * Creates a hexadecimal color based on gave params
     * All values need to be in a range of 0 ~ 255
     * @param a Alpha
     * @param r Red
     * @param g Green
     * @param b Blue
     * @return HEX color
     */
    public static int getColorARGB(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }
}