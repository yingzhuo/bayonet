package com.github.yingzhuo.bayonet.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * JWT 声明（Claim）接口。
 * <p>提供对单个声明值的类型化读取方法。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Claim {

    /**
     * 判断该声明是否为 {@code null} 值。
     * <p>若声明不存在，返回 {@code false}；建议同时检查 {@link #isMissing()}。</p>
     *
     * @return {@code true} 表示声明值为 {@code null}
     */
    boolean isNull();

    /**
     * 判断声明是否存在。
     * <p>即使声明关联 {@code null} 值，也会返回 {@code true}。</p>
     *
     * @return {@code true} 表示声明存在
     */
    boolean isMissing();

    /**
     * 将该声明读取为 {@link Boolean}。
     * <p>若值不是 {@code Boolean} 类型或无法转换，返回 {@code null}。</p>
     *
     * @return {@code Boolean} 值或 {@code null}
     */
    Boolean asBoolean();

    /**
     * 将该声明读取为 {@link Integer}。
     * <p>若值不是 {@code Integer} 类型或无法转换，返回 {@code null}。</p>
     *
     * @return {@code Integer} 值或 {@code null}
     */
    Integer asInt();

    /**
     * 将该声明读取为 {@link Long}。
     * <p>若值不是 {@code Long} 类型或无法转换，返回 {@code null}。</p>
     *
     * @return {@code Long} 值或 {@code null}
     */
    Long asLong();

    /**
     * 将该声明读取为 {@link Double}。
     * <p>若值不是 {@code Double} 类型或无法转换，返回 {@code null}。</p>
     *
     * @return {@code Double} 值或 {@code null}
     */
    Double asDouble();

    /**
     * 将该声明读取为 {@link String}。
     * <p>若值不是 {@code String} 类型，返回 {@code null}。
     * 对非文本类型的声明，可调用 {@code toString()} 获取字符串表示。</p>
     *
     * @return {@code String} 值或 {@code null}
     */
    String asString();

    /**
     * 将该声明读取为 {@link LocalDateTime}。
     * <p>若值无法转换为 {@link LocalDateTime}，返回 {@code null}。</p>
     *
     * @return {@link LocalDateTime} 值或 {@code null}
     */
    LocalDateTime asLocalDateTime();

    /**
     * 将该声明读取为 {@link Instant}（使用系统默认时区）。
     * <p>若值无法转换为 {@link Instant}，返回 {@code null}。</p>
     *
     * @return {@link Instant} 值或 {@code null}
     */
    default Instant asInstant() {
        var localDateTime = asLocalDateTime();
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    /**
     * 将该声明读取为类型 {@code T} 的数组。
     * <p>若值不是数组，返回 {@code null}。</p>
     *
     * @param <T>   元素类型
     * @param clazz 元素类型 Class
     * @return 类型 {@code T} 的数组或 {@code null}
     */
    <T> T[] asArray(Class<T> clazz);

    /**
     * 将该声明读取为类型 {@code T} 的列表。
     * <p>若值不是数组，返回 {@code null}。</p>
     *
     * @param <T>   元素类型
     * @param clazz 元素类型 Class
     * @return 类型 {@code T} 的列表或 {@code null}
     */
    <T> List<T> asList(Class<T> clazz);

    /**
     * 将该声明读取为泛型 {@link Map}。
     *
     * @return 值的 {@link Map} 表示
     */
    Map<String, Object> asMap();
}
