package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * {@link LocalDateTime} 工具类。
 *
 * <p>提供格式化、字符串解析、时间距离计算、年龄计算及午夜时间获取等常用操作。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * String str = LocalDateTimeUtils.formatToShort(LocalDateTime.now());
 * LocalDateTime dt = LocalDateTimeUtils.convertFromString("2026-07-23 10:30:00");
 * long days = LocalDateTimeUtils.getDayDistance(dt1, dt2);
 * Integer age = LocalDateTimeUtils.getAge(LocalDate.of(2000, 1, 1));
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LocalDateTimeUtils {

    /**
     * 尝试解析日期时间字符串时使用的格式数组。
     * <p>包括常见的时间戳格式（yyyy-MM-dd HH:mm:ss 及其带毫秒、斜杠分隔、T 分隔变体）。</p>
     */
    public static final String[] TRYING_DATE_TIME_CONVERSION_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS"
    };

    private static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 格式化为短格式（{@code yyyy-MM-dd}）。
     *
     * @param localDateTime 日期时间，不能为 {@code null}
     * @return 格式化后的字符串
     */
    public static String formatToShort(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime must not be null");
        return localDateTime.format(SHORT_FORMATTER);
    }

    /**
     * 格式化为长格式（{@code yyyy-MM-dd HH:mm:ss}）。
     *
     * @param localDateTime 日期时间，不能为 {@code null}
     * @return 格式化后的字符串
     */
    public static String formatToLong(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime must not be null");
        return localDateTime.format(LONG_FORMATTER);
    }

    // ------

    /**
     * 将字符串解析为 {@link LocalDateTime}（使用默认格式数组）。
     *
     * <p>依次尝试：</p>
     * <ol>
     *   <li>数字字符串尝试解析为时间戳（10 位秒级、其余毫秒级）</li>
     *   <li>各日期时间格式（见 {@link #TRYING_DATE_TIME_CONVERSION_PATTERNS}）</li>
     * </ol>
     *
     * @param text 日期时间字符串，不能为 {@code null} 或空
     * @return {@link LocalDateTime} 实例
     * @throws DateTimeParseException 全部格式均无法解析时抛出
     */
    public static LocalDateTime convertFromString(String text) {
        return convertFromString(text, TRYING_DATE_TIME_CONVERSION_PATTERNS);
    }

    /**
     * 将字符串解析为 {@link LocalDateTime}（使用自定义格式数组）。
     *
     * @param text     日期时间字符串，不能为 {@code null} 或空
     * @param patterns 日期时间格式数组，可为 {@code null}
     * @return {@link LocalDateTime} 实例
     * @throws DateTimeParseException 全部格式均无法解析时抛出
     */
    public static LocalDateTime convertFromString(String text, @Nullable String... patterns) {
        Assert.hasText(text, "text must not be empty");

        patterns = Objects.requireNonNullElse(patterns, new String[0]);

        var formatters = Arrays.stream(patterns)
                .filter(Objects::nonNull)
                .map(DateTimeFormatter::ofPattern)
                .toList();

        return convertFromString(text, formatters);
    }

    /**
     * 将字符串解析为 {@link LocalDateTime}（使用自定义 {@link DateTimeFormatter} 列表）。
     *
     * @param text       日期时间字符串，不能为 {@code null} 或空
     * @param formatters 格式化器列表，可为 {@code null}
     * @return {@link LocalDateTime} 实例
     * @throws DateTimeParseException 全部格式均无法解析时抛出
     */
    public static LocalDateTime convertFromString(String text, @Nullable List<DateTimeFormatter> formatters) {
        Assert.hasText(text, "text must not be empty");

        formatters = Objects.requireNonNullElse(formatters, List.of());

        try {
            Long timestamp = Long.parseLong(text);
            if (timestamp.toString().length() == 10) {
                return LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(timestamp),
                        ZoneId.systemDefault()
                );
            } else {
                return LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                );
            }
        } catch (NumberFormatException ignored) {
            // 不是整型数
        }

        for (var formatter : formatters) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // 一个不行就尝试下一个
            }
        }

        throw new DateTimeParseException("Cannot parse text to LocalDateTime", text, 0);
    }

    // ------

    /**
     * 计算两个日期时间之间的年数差（绝对值）。
     *
     * @param d1 日期时间 1，不能为 {@code null}
     * @param d2 日期时间 2，不能为 {@code null}
     * @return 年数差（绝对值）
     */
    public static long getYearDistance(LocalDateTime d1, LocalDateTime d2) {
        Assert.notNull(d1, "d1 must not be null");
        Assert.notNull(d2, "d2 must not be null");
        return Math.abs(ChronoUnit.YEARS.between(d1, d2));
    }

    /**
     * 计算两个日期时间之间的天数差（绝对值）。
     *
     * @param d1 日期时间 1，不能为 {@code null}
     * @param d2 日期时间 2，不能为 {@code null}
     * @return 天数差（绝对值）
     */
    public static long getDayDistance(LocalDateTime d1, LocalDateTime d2) {
        Assert.notNull(d1, "d1 must not be null");
        Assert.notNull(d2, "d2 must not be null");
        return Math.abs(ChronoUnit.DAYS.between(d1, d2));
    }

    // ------

    /**
     * 根据出生日期计算年龄。
     *
     * @param dob 出生日期时间，可为 {@code null}
     * @return 年龄，输入为 {@code null} 时返回 {@code null}
     */
    @Nullable
    public static Integer getAge(@Nullable LocalDateTime dob) {
        if (dob == null) {
            return null;
        }
        return (int) getYearDistance(dob, LocalDateTime.now());
    }

    /**
     * 根据出生日期计算年龄。
     *
     * @param dob 出生日期，可为 {@code null}
     * @return 年龄，输入为 {@code null} 时返回 {@code null}
     */
    @Nullable
    public static Integer getAge(@Nullable LocalDate dob) {
        if (dob == null) {
            return null;
        }
        return getAge(dob.atStartOfDay());
    }

    // ------

    /**
     * 获取当天午夜（00:00:00）。
     *
     * @param dateTime 日期时间，不能为 {@code null}
     * @return 当天午夜
     */
    public static LocalDateTime toThisDayMidnight(LocalDateTime dateTime) {
        Assert.notNull(dateTime, "dateTime must not be null");
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    /**
     * 获取次日午夜（00:00:00）。
     *
     * @param dateTime 日期时间，不能为 {@code null}
     * @return 次日午夜
     */
    public static LocalDateTime toNextDayMidnight(LocalDateTime dateTime) {
        Assert.notNull(dateTime, "dateTime must not be null");
        return dateTime.plusDays(1).truncatedTo(ChronoUnit.DAYS);
    }

}
