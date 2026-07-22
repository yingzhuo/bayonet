package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机字符串工具类。
 *
 * <p>提供多种常见随机字符串生成方法，支持字母、数字、字母数字组合、ASCII 可打印字符等。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * String s1 = RandomStringUtils.randomAlphabetic(10);
 * String s2 = RandomStringUtils.randomAlphanumeric(8);
 * String s3 = RandomStringUtils.randomNumeric(6);
 * String s4 = RandomStringUtils.randomAscii(16);
 * }</pre>
 *
 * @author 应卓
 * @see RandomUtils
 * @see ThreadLocalRandom
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomStringUtils {

    /**
     * 生成指定长度的随机字符串（字母或数字）。
     *
     * @param count 长度
     * @return 随机字符串
     */
    public static String random(final int count) {
        return random(count, false, false);
    }

    /**
     * 生成指定长度的随机 ASCII 字符串。
     *
     * @param count 长度
     * @return 随机字符串
     */
    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * 生成随机 ASCII 字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomAscii(int minLengthInclusive, int maxLengthExclusive) {
        return randomAscii(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机字母字符串。
     *
     * @param count 长度
     * @return 随机字母字符串
     */
    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    /**
     * 生成随机字母字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机字母字符串
     */
    public static String randomAlphabetic(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphabetic(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机字母数字字符串。
     *
     * @param count 长度
     * @return 随机字母数字字符串
     */
    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    /**
     * 生成随机字母数字字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机字母数字字符串
     */
    public static String randomAlphanumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphanumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机图形字符字符串。
     *
     * @param count 长度
     * @return 随机图形字符字符串
     */
    public static String randomGraph(int count) {
        return random(count, 33, 126, false, false);
    }

    /**
     * 生成随机图形字符字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机图形字符字符串
     */
    public static String randomGraph(int minLengthInclusive, int maxLengthExclusive) {
        return randomGraph(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机数字字符串。
     *
     * @param count 长度
     * @return 随机数字字符串
     */
    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    /**
     * 生成随机数字字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机数字字符串
     */
    public static String randomNumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomNumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机可打印字符字符串。
     *
     * @param count 长度
     * @return 随机可打印字符字符串
     */
    public static String randomPrint(int count) {
        return random(count, 32, 126, false, false);
    }

    /**
     * 生成随机可打印字符字符串（指定长度范围）。
     *
     * @param minLengthInclusive 最小长度（包含）
     * @param maxLengthExclusive 最大长度（不包含）
     * @return 随机可打印字符字符串
     */
    public static String randomPrint(int minLengthInclusive, int maxLengthExclusive) {
        return randomPrint(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 生成指定长度的随机字符串（控制字母和数字）。
     *
     * @param count   长度
     * @param letters 是否包含字母
     * @param numbers 是否包含数字
     * @return 随机字符串
     */
    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * 生成随机字符串（指定字符范围）。
     *
     * @param count   长度
     * @param start   起始码点（包含）
     * @param end     结束码点（不包含）
     * @param letters 是否包含字母
     * @param numbers 是否包含数字
     * @return 随机字符串
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, ThreadLocalRandom.current());
    }

    // --------------------------------------------------------------

    private static String random(int count, int start, int end, boolean letters, boolean numbers,
                                 char @Nullable [] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else if (!letters && !numbers) {
                end = Character.MAX_CODE_POINT;
            } else {
                end = 'z' + 1;
                start = ' ';
            }
        } else if (end <= start) {
            throw new IllegalArgumentException(
                    "Parameter end (" + end + ") must be greater than start (" + start + ")");
        }

        if (chars == null && (numbers && end <= '0' || letters && end <= 'A')) {
            throw new IllegalArgumentException(
                    "Parameter end (" + end + ") must be greater then (" + '0' + ") for generating digits "
                            + "or greater then (" + 'A' + ") for generating letters.");
        }

        StringBuilder builder = new StringBuilder(count);
        int gap = end - start;

        while (count-- != 0) {
            int codePoint;
            if (chars == null) {
                codePoint = random.nextInt(gap) + start;

                switch (Character.getType(codePoint)) {
                    case Character.UNASSIGNED:
                    case Character.PRIVATE_USE:
                    case Character.SURROGATE:
                        count++;
                        continue;
                }

            } else {
                codePoint = chars[random.nextInt(gap) + start];
            }

            int numberOfChars = Character.charCount(codePoint);
            if (count == 0 && numberOfChars > 1) {
                count++;
                continue;
            }

            if (letters && Character.isLetter(codePoint) || numbers && Character.isDigit(codePoint)
                    || !letters && !numbers) {
                builder.appendCodePoint(codePoint);

                if (numberOfChars == 2) {
                    count--;
                }

            } else {
                count++;
            }
        }
        return builder.toString();
    }

    /**
     * 从指定字符集生成随机字符串。
     *
     * @param count 长度
     * @param chars 字符集字符串，为 {@code null} 时使用默认字符集
     * @return 随机字符串
     */
    public static String random(int count, @Nullable String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, ThreadLocalRandom.current());
        }
        return random(count, chars.toCharArray());
    }

    /**
     * 从指定字符数组生成随机字符串。
     *
     * @param count 长度
     * @param chars 字符数组，为 {@code null} 时使用默认字符集
     * @return 随机字符串
     */
    public static String random(int count, @Nullable char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, ThreadLocalRandom.current());
        }
        return random(count, 0, chars.length, false, false, chars, ThreadLocalRandom.current());
    }
}
