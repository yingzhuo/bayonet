package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * {@link SslBundle} 查找工具类。
 *
 * <p>通过 {@link SpringUtils} 获取 {@link SslBundles} Bean，提供便捷的 {@link SslBundle} 查找方法，
 * 支持不存在时返回 {@code null}、指定默认值或延迟计算默认值等模式。</p>
 *
 * @author 应卓
 * @see SpringUtils
 * @see SslBundles
 * @see SslBundle
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SslBundleUtils {

    /**
     * 按名称查找 {@link SslBundle}，不存在时返回 {@code null}。
     *
     * @param name SSL Bundle 名称（非空）
     * @return {@link SslBundle} 或 {@code null}
     */
    @Nullable
    public static SslBundle getOrNull(String name) {
        Assert.hasText(name, "name must not be empty");

        var bundles = SpringUtils.getApplicationContext()
                .getBeanProvider(SslBundles.class)
                .getIfAvailable();

        if (bundles == null) return null;

        try {
            return bundles.getBundle(name);
        } catch (NoSuchSslBundleException e) {
            return null;
        }
    }

    /**
     * 按名称查找 {@link SslBundle}，不存在时返回默认值。
     *
     * @param name          SSL Bundle 名称（非空）
     * @param defaultBundle 默认值（非 {@code null}）
     * @return {@link SslBundle}（非 {@code null}）
     */
    public static SslBundle getOrElse(String name, SslBundle defaultBundle) {
        return Optional.ofNullable(getOrNull(name))
                .orElse(defaultBundle);
    }

    /**
     * 按名称查找 {@link SslBundle}，不存在时通过 {@link Supplier} 延迟计算默认值。
     *
     * @param name                  SSL Bundle 名称（非空）
     * @param defaultBundleSupplier 默认值提供者（非 {@code null}）
     * @return {@link SslBundle}（非 {@code null}）
     */
    public static SslBundle getOrElseGet(String name, Supplier<SslBundle> defaultBundleSupplier) {
        Assert.notNull(defaultBundleSupplier, "defaultBundleSupplier must not be null");

        return Optional.ofNullable(getOrNull(name))
                .orElseGet(defaultBundleSupplier);
    }

}
