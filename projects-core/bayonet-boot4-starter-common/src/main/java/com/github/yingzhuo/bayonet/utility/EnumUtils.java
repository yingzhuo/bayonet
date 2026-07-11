package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举工具类。
 * <p>提供按名称查找、忽略大小写查找、列表/Map 转换、有效性判断等便捷方法。</p>
 *
 * <pre>{@code
 * MyEnum value = EnumUtils.getEnum(MyEnum.class, "FOO");
 * MyEnum fallback = EnumUtils.getEnum(MyEnum.class, "unknown", MyEnum.DEFAULT);
 * boolean valid = EnumUtils.isValidEnum(MyEnum.class, "FOO");
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumUtils {

    /**
     * 按枚举名称查找枚举常量。
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param <E>       枚举类型
     * @return 枚举常量
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}、{@code enumName} 为空或名称不存在
     */
    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");
        return Enum.valueOf(enumClass, enumName);
    }

    /**
     * 按枚举名称查找枚举常量，查找失败时返回默认值。
     *
     * @param enumClass   枚举类
     * @param enumName    枚举名称
     * @param defaultEnum 默认值
     * @param <E>         枚举类型
     * @return 枚举常量，名称不存在时返回 {@code defaultEnum}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}、{@code enumName} 为空或 {@code defaultEnum} 为 {@code null}
     */
    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName, E defaultEnum) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");
        Assert.notNull(defaultEnum, "defaultEnum is required");

        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (IllegalArgumentException ex) {
            return defaultEnum;
        }
    }

    /**
     * 按枚举名称查找枚举常量（忽略大小写）。
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param <E>       枚举类型
     * @return 枚举常量
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}、{@code enumName} 为空或名称不存在
     */
    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> enumClass, String enumName) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");

        for (E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(enumName)) {
                return each;
            }
        }

        throw new IllegalArgumentException("'" + enumName + "' is not a valid enum name.");
    }

    /**
     * 按枚举名称查找枚举常量（忽略大小写），查找失败时返回默认值。
     *
     * @param enumClass   枚举类
     * @param enumName    枚举名称
     * @param defaultEnum 默认值
     * @param <E>         枚举类型
     * @return 枚举常量，名称不存在时返回 {@code defaultEnum}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}、{@code enumName} 为空或 {@code defaultEnum} 为 {@code null}
     */
    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> enumClass, String enumName, E defaultEnum) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");
        Assert.notNull(defaultEnum, "defaultEnum is required");

        for (E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(enumName)) {
                return each;
            }
        }

        return defaultEnum;
    }

    /**
     * 获取枚举类的所有常量列表。
     *
     * @param enumClass 枚举类
     * @param <E>       枚举类型
     * @return 枚举常量列表，不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}
     */
    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
        Assert.notNull(enumClass, "enumClass is required");
        return Arrays.asList(enumClass.getEnumConstants());
    }

    /**
     * 将枚举类转换为 {@code name → enum} 的 {@link LinkedHashMap}。
     *
     * @param enumClass 枚举类
     * @param <E>       枚举类型
     * @return 名称到枚举常量的 Map（保持声明顺序），不会为 {@code null}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null}
     */
    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumClass) {
        Assert.notNull(enumClass, "enumClass is required");

        Map<String, E> map = new LinkedHashMap<>();
        for (E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    /**
     * 判断枚举名称是否有效。
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param <E>       枚举类型
     * @return 名称有效返回 {@code true}，否则 {@code false}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null} 或 {@code enumName} 为空
     */
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");

        return getEnumList(enumClass).stream().anyMatch(e -> e.name().equals(enumName));
    }

    /**
     * 判断枚举名称是否有效（忽略大小写）。
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param <E>       枚举类型
     * @return 名称有效返回 {@code true}，否则 {@code false}
     * @throws IllegalArgumentException 若 {@code enumClass} 为 {@code null} 或 {@code enumName} 为空
     */
    public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(Class<E> enumClass, String enumName) {
        Assert.notNull(enumClass, "enumClass is required");
        Assert.hasText(enumName, "enumName is required");

        return getEnumList(enumClass).stream().anyMatch(e -> e.name().equalsIgnoreCase(enumName));
    }

}
