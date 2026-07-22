package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具类。
 *
 * <p>基于 {@link ThreadLocalRandom} 实现，提供线程安全的各种类型随机数生成。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * boolean b = RandomUtils.nextBoolean();
 * int i = RandomUtils.nextInt(1, 100);
 * long l = RandomUtils.nextLong(1, 100);
 * double d = RandomUtils.nextDouble(0.0, 1.0);
 * }</pre>
 *
 * @author 应卓
 * @see ThreadLocalRandom
 * @see RandomStringUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtils {

    /**
     * 随机布尔值。
     *
     * @return {@code true} 或 {@code false}
     */
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 随机字节数组。
     *
     * @param count 字节数，不能为负数
     * @return 随机字节数组
     */
    public static byte[] nextBytes(int count) {
        byte[] result = new byte[count];
        ThreadLocalRandom.current().nextBytes(result);
        return result;
    }

    /**
     * 随机整数（指定范围）。
     *
     * @param startInclusive 起始值（包含）
     * @param endExclusive   结束值（不包含）
     * @return {@code [startInclusive, endExclusive)} 范围内的随机整数
     */
    public static int nextInt(int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ThreadLocalRandom.current().nextInt(endExclusive - startInclusive);
    }

    /**
     * 随机整数（非负）。
     *
     * @return {@code [0, Integer.MAX_VALUE)} 范围内的随机整数
     */
    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    /**
     * 随机长整数（指定范围）。
     *
     * @param startInclusive 起始值（包含）
     * @param endExclusive   结束值（不包含）
     * @return {@code [startInclusive, endExclusive)} 范围内的随机长整数
     */
    public static long nextLong(long startInclusive, long endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ThreadLocalRandom.current().nextLong(endExclusive - startInclusive);
    }

    /**
     * 随机长整数（非负）。
     *
     * @return {@code [0, Long.MAX_VALUE)} 范围内的随机长整数
     */
    public static long nextLong() {
        return ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
    }

    /**
     * 随机双精度浮点数（指定范围）。
     *
     * @param startInclusive 起始值（包含）
     * @param endExclusive   结束值（不包含）
     * @return {@code [startInclusive, endExclusive)} 范围内的随机双精度浮点数
     */
    public static double nextDouble(double startInclusive, double endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * ThreadLocalRandom.current().nextDouble());
    }

    /**
     * 随机双精度浮点数（非负）。
     *
     * @return {@code [0, Double.MAX_VALUE)} 范围内的随机双精度浮点数
     */
    public static double nextDouble() {
        return nextDouble(0, Double.MAX_VALUE);
    }

    /**
     * 随机单精度浮点数（指定范围）。
     *
     * @param startInclusive 起始值（包含）
     * @param endExclusive   结束值（不包含）
     * @return {@code [startInclusive, endExclusive)} 范围内的随机单精度浮点数
     */
    public static float nextFloat(float startInclusive, float endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * ThreadLocalRandom.current().nextFloat());
    }

    /**
     * 随机单精度浮点数（非负）。
     *
     * @return {@code [0, Float.MAX_VALUE)} 范围内的随机单精度浮点数
     */
    public static float nextFloat() {
        return nextFloat(0, Float.MAX_VALUE);
    }
}
