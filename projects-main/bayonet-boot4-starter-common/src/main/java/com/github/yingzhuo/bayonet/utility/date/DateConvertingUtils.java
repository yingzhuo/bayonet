package com.github.yingzhuo.bayonet.utility.date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具类。
 * <p>提供 {@link LocalDateTime}、{@link Calendar} 到 {@link Date} 的转换方法。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateConvertingUtils {

    /**
     * 将 {@link LocalDateTime} 转换为 {@link Date}。
     * <p>使用系统默认时区。</p>
     *
     * @param localDateTime 待转换时间（非 {@code null}）
     * @return 转换结果（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code localDateTime} 为 {@code null}
     */
    public static Date toDate(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime must not be null");
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 {@link Calendar} 转换为 {@link Date}。
     *
     * @param calendar 待转换日历（非 {@code null}）
     * @return 转换结果（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code calendar} 为 {@code null}
     */
    public static Date toDate(Calendar calendar) {
        Assert.notNull(calendar, "calendar must not be null");
        return Date.from(calendar.toInstant());
    }
}
