package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.ConversionService;

/**
 * {@link ConversionService} 工具类。
 * <p>提供从 Spring 应用上下文获取 {@link ConversionService} 的静态快捷方法。</p>
 *
 * @author 应卓
 * @see ConversionService
 * @see SpringUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConversionServiceUtils {

    /**
     * 获取 Spring 应用上下文中的 {@link ConversionService}。
     *
     * @return ConversionService 实例（非 {@code null}）
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 若 Bean 不存在
     */
    public static ConversionService getConversionService() {
        return SpringUtils.getBean(ConversionService.class);
    }

    /**
     * 判断是否支持从源类型到目标类型的转换。
     *
     * @param from 源类型
     * @param to   目标类型
     * @return 是否支持转换
     */
    public static boolean canConvert(Class<?> from, Class<?> to) {
        return getConversionService().canConvert(from, to);
    }

    /**
     * 转换对象到目标类型。
     *
     * @param from 源对象，可为 {@code null}
     * @param to   目标类型
     * @param <T>  目标类型
     * @return 转换后的对象，可为 {@code null}
     */
    public static <T> @Nullable T convert(@Nullable Object from, Class<T> to) {
        return getConversionService().convert(from, to);
    }
}
