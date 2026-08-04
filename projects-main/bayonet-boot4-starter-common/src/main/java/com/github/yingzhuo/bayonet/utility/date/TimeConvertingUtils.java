package com.github.yingzhuo.bayonet.utility.date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间类型转换工具类。
 *
 * <p>提供 {@link Date}、{@link Calendar} 到 {@link LocalDate}/{@link LocalDateTime}/{@link ZonedDateTime} 的转换。
 * 不指定时区时使用系统默认时区。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * LocalDate date1 = TimeConvertingUtils.toLocalDate(new Date());
 * LocalDate date2 = TimeConvertingUtils.toLocalDate(Calendar.getInstance());
 * LocalDateTime dt1 = TimeConvertingUtils.toLocalDateTime(new Date());
 * LocalDateTime dt2 = TimeConvertingUtils.toLocalDateTime(Calendar.getInstance());
 * ZonedDateTime zdt1 = TimeConvertingUtils.toZonedDateTime(new Date(), ZoneId.of("Asia/Shanghai"));
 * ZonedDateTime zdt2 = TimeConvertingUtils.toZonedDateTime(Calendar.getInstance());
 * }</pre>
 *
 * @author 应卓
 * @see LocalDateTimeUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeConvertingUtils {

    /**
     * 将 {@link Date} 转换为 {@link LocalDate}（使用系统默认时区）。
     *
     * @param date 日期，不能为 {@code null}
     * @return {@link LocalDate} 实例（非 {@code null}）
     */
    public static LocalDate toLocalDate(Date date) {
        Assert.notNull(date, "date must not be null");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 {@link Calendar} 转换为 {@link LocalDate}（使用系统默认时区）。
     *
     * @param calendar 日历，不能为 {@code null}
     * @return {@link LocalDate} 实例（非 {@code null}）
     */
    public static LocalDate toLocalDate(Calendar calendar) {
        Assert.notNull(calendar, "calendar must not be null");
        return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 {@link Date} 转换为 {@link LocalDateTime}（使用系统默认时区）。
     *
     * @param date 日期，不能为 {@code null}
     * @return {@link LocalDateTime} 实例（非 {@code null}）
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Assert.notNull(date, "date must not be null");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 {@link Calendar} 转换为 {@link LocalDateTime}（使用系统默认时区）。
     *
     * @param calendar 日历，不能为 {@code null}
     * @return {@link LocalDateTime} 实例（非 {@code null}）
     */
    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        Assert.notNull(calendar, "calendar must not be null");
        return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 {@link Date} 转换为 {@link ZonedDateTime}（使用系统默认时区）。
     *
     * @param date 日期，不能为 {@code null}
     * @return {@link ZonedDateTime} 实例（非 {@code null}）
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return toZonedDateTime(date, null);
    }

    /**
     * 将 {@link Date} 转换为 {@link ZonedDateTime}。
     *
     * @param date 日期，不能为 {@code null}
     * @param zone 时区，为 {@code null} 时使用系统默认时区
     * @return {@link ZonedDateTime} 实例（非 {@code null}）
     */
    public static ZonedDateTime toZonedDateTime(Date date, @Nullable ZoneId zone) {
        Assert.notNull(date, "date must not be null");
        return date.toInstant().atZone(zone != null ? zone : ZoneId.systemDefault());
    }

    /**
     * 将 {@link Calendar} 转换为 {@link ZonedDateTime}（使用系统默认时区）。
     *
     * @param calendar 日历，不能为 {@code null}
     * @return {@link ZonedDateTime} 实例（非 {@code null}）
     */
    public static ZonedDateTime toZonedDateTime(Calendar calendar) {
        return toZonedDateTime(calendar, null);
    }

    /**
     * 将 {@link Calendar} 转换为 {@link ZonedDateTime}。
     *
     * @param calendar 日历，不能为 {@code null}
     * @param zone     时区，为 {@code null} 时使用系统默认时区
     * @return {@link ZonedDateTime} 实例（非 {@code null}）
     */
    public static ZonedDateTime toZonedDateTime(Calendar calendar, @Nullable ZoneId zone) {
        Assert.notNull(calendar, "calendar must not be null");
        return calendar.toInstant().atZone(zone != null ? zone : ZoneId.systemDefault());
    }

}
