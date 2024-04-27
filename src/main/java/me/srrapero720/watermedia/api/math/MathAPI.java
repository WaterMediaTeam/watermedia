package me.srrapero720.watermedia.api.math;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.loaders.ILoader;

public class MathAPI extends WaterMediaAPI {

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Tick count
     * @return ticks converted to MS
     * @deprecated <p>Tick type was changed from int to long following the time counting standard.</p>
     * Use instead {@link MathAPI#tickToMs(long)}
     */
    public static long tickToMs(int ticks) { return ticks * 50L; }

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Tick count
     * @return ticks converted to MS
     */
    public static long tickToMs(long ticks) { return ticks * 50L; }

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param partialTicks Minecraft Partial tick count
     * @return ticks converted to MS
     */
    public static long tickToMs(float partialTicks) { return (long) (partialTicks * 50L); }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int msToTick(long ms) { return (int) (ms / 50); }

    /**
     * Missing docs
     * @param start
     * @param end
     * @param time
     * @return
     */
    public static double scaleTempo(long start, long end, long time) {
        if (start < 0 || end < 0 || time < 0) throw new IllegalArgumentException("Invalid negative value");
        try {
            long duration = end - start; // start acts like a margin
            long realTime = time - start;

            long result = realTime / duration;
            if (realTime > duration) result %= end - start;
            return result;
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    public static double scaleTempo(double start, double end, double time) {
        if (start < 0 || end < 0 || time < 0) throw new IllegalArgumentException("Invalid negative value");
        try {
            double duration = end - start; // start acts like a margin
            double realTime = time - start;

            double result = realTime / duration;
            if (realTime > duration) result %= end - start;
            return result;
        } catch (ArithmeticException ignored) {
            return 0;
        }
    }

    /**
     * missing docs
     * @param startTick
     * @param endTick
     * @param timeTick
     * @return
     */
    public static double scaleTempoTick(int startTick, int endTick, int timeTick) {
        return scaleTempo(MathAPI.tickToMs(startTick), MathAPI.tickToMs(endTick), MathAPI.tickToMs(timeTick));
    }

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
        return (int) floorMod(x, (long)y);
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

    /**
     * Converts arguments into an ease-in value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculated result of ease-in
     */
    public static double easeIn(double start, double end, double t) {
        return start + (end - start) * t * t;
    }

    /**
     * Converts arguments into an ease-out value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of ease-out
     */
    public static double easeOut(double start, double end, double t) {
        return start + (end - start) * (1 - Math.pow(1 - t, 2));
    }

    /**
     * Converts arguments into an ease-in-out value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of ease-in-out
     */
    public static double easeInOut(double start, double end, double t) {
        return t < 0.5 ? easeIn(start, end / 2, t * 2) : easeOut(start + (end / 2), end, (t - 0.5) * 2);
    }

    /**
     * Converts arguments into an ease-out-in value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of ease-out-in
     */
    public static double easeOutIn(double start, double end, double t) {
        return t < 0.5 ? easeOut(start, end / 2, t * 2) : easeIn(start + (end / 2), end, (t - 0.5) * 2);
    }

    /**
     * Converts arguments into an ease-in-circle value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of ease-in-circle
     */
    public static double easeInCircle(double start, double end, double t) {
        return start + (end - start) * (1 - Math.sqrt(1 - t * t));
    }

    /**
     * Converts arguments into an ease-out-circle value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of ease-out-circle
     */
    public static double easeOutCircle(double start, double end, double t) {
        return start + (end - start) * Math.sqrt(1 - Math.pow(t - 1, 2));
    }

    /**
     * Converts arguments into an easy-ease value usable on animations
     * @param start begin of the result across time
     * @param end end of the result across time
     * @param t time from 0.0 ~ 1.0
     * @return calculate result of easy-ease
     */
    public static double easyEase(double start, double end, double t) {
        return start + (end - start) * ((t < 0.5) ? 2 * t * t : -1 + 2 * t * (2 - t));
    }

    @Override
    public Priority priority() {
        return Priority.LOWEST;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return false;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}