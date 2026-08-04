package com.github.yingzhuo.bayonet.utility.date;

import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 十二星座枚举。
 * <p>可通过 {@link #of(int, int)} 或各种日期类型判断所属星座。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public enum Zodiac {

    /**
     * 白羊座（3 月 21 日 - 4 月 19 日）。
     */
    ARIES,

    /**
     * 金牛座（4 月 20 日 - 5 月 20 日）。
     */
    TAURUS,

    /**
     * 双子座（5 月 21 日 - 6 月 21 日）。
     */
    GEMINI,

    /**
     * 巨蟹座（6 月 22 日 - 7 月 22 日）。
     */
    CANCER,

    /**
     * 狮子座（7 月 23 日 - 8 月 22 日）。
     */
    LEO,

    /**
     * 处女座（8 月 23 日 - 9 月 22 日）。
     */
    VIRGO,

    /**
     * 天秤座（9 月 23 日 - 10 月 23 日）。
     */
    LIBRA,

    /**
     * 天蝎座（10 月 24 日 - 11 月 22 日）。
     */
    SCORPIO,

    /**
     * 射手座（11 月 23 日 - 12 月 21 日）。
     */
    SAGITTARIUS,

    /**
     * 摩羯座（12 月 22 日 - 1 月 19 日）。
     */
    CAPRICORN,

    /**
     * 水瓶座（1 月 20 日 - 2 月 18 日）。
     */
    AQUARIUS,

    /**
     * 双鱼座（2 月 19 日 - 3 月 20 日）。
     */
    PISCES;

    /**
     * 根据月/日判断所属星座。
     *
     * @param month 月份（1 - 12）
     * @param day   日期（1 - 31）
     * @return 星座（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code month} 不在 1 - 12 范围
     */
    public static Zodiac of(int month, int day) {
        return switch (month) {
            case 1 -> day >= 20 ? AQUARIUS : CAPRICORN;
            case 2 -> day >= 19 ? PISCES : AQUARIUS;
            case 3 -> day >= 21 ? ARIES : PISCES;
            case 4 -> day >= 20 ? TAURUS : ARIES;
            case 5 -> day >= 21 ? GEMINI : TAURUS;
            case 6 -> day >= 21 ? CANCER : GEMINI;
            case 7 -> day >= 23 ? LEO : CANCER;
            case 8 -> day >= 23 ? VIRGO : LEO;
            case 9 -> day >= 23 ? LIBRA : VIRGO;
            case 10 -> day >= 23 ? SCORPIO : LIBRA;
            case 11 -> day >= 22 ? SAGITTARIUS : SCORPIO;
            case 12 -> day >= 22 ? CAPRICORN : SAGITTARIUS;
            default -> throw new IllegalArgumentException("invalid day or month");
        };
    }

    /**
     * 根据 {@link Date} 判断所属星座（系统默认时区）。
     *
     * @param date 日期（非 {@code null}）
     * @return 星座（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code date} 为 {@code null}
     */
    public static Zodiac of(Date date) {
        Assert.notNull(date, "date must not be null");
        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        return of(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 根据 {@link Calendar} 判断所属星座。
     *
     * @param calendar 日历（非 {@code null}）
     * @return 星座（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code calendar} 为 {@code null}
     */
    public static Zodiac of(Calendar calendar) {
        Assert.notNull(calendar, "calendar must not be null");
        return of(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 根据 {@link LocalDate} 判断所属星座。
     *
     * @param localDate 日期（非 {@code null}）
     * @return 星座（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code localDate} 为 {@code null}
     */
    public static Zodiac of(LocalDate localDate) {
        Assert.notNull(localDate, "localDate must not be null");
        return of(localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    /**
     * 根据 {@link LocalDateTime} 判断所属星座。
     *
     * @param localDateTime 日期时间（非 {@code null}）
     * @return 星座（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code localDateTime} 为 {@code null}
     */
    public static Zodiac of(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime must not be null");
        return of(localDateTime.toLocalDate());
    }
}
