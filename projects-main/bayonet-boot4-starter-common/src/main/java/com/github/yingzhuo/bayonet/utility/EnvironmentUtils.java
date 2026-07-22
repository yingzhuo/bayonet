package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;

/**
 * Spring {@link Environment} 工具类。
 * <p>提供便捷的静态方法访问 {@link Environment} 并执行属性读取、占位符解析、Profile 判断等操作。
 * 在 Spring 应用上下文就绪前自动降级为 {@link StandardEnvironment}，支持冷启动阶段使用。</p>
 *
 * <pre>{@code
 * // 获取系统属性
 * String javaVersion = EnvironmentUtils.getProperty("java.version");
 *
 * // 解析占位符
 * String resolved = EnvironmentUtils.resolvePlaceholders("${app.name:default}");
 *
 * // 判断 Profile
 * boolean isDev = EnvironmentUtils.acceptsProfiles(Profiles.of("dev"));
 * }</pre>
 *
 * @author 应卓
 * @see Environment
 * @see StandardEnvironment
 * @see SpringUtils
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentUtils {

    /**
     * 获取 {@link Environment} 实例。
     * <p>优先返回 Spring 应用上下文中的 {@link Environment} Bean，
     * 若上下文尚未就绪则降级为 {@link StandardEnvironment}。</p>
     *
     * @return Environment 实例（非 {@code null}）
     */
    public static Environment getEnvironment() {
        try {
            return SpringUtils.getBean(Environment.class);
        } catch (IllegalStateException e) {
            return LazyHolder.DEFAULT_ENVIRONMENT;
        }
    }

    /**
     * 判断当前环境是否激活了指定的 Profile 组合。
     *
     * @param profiles Profile 组合
     * @return 若匹配任一 Profile 返回 {@code true}
     */
    public static boolean acceptsProfiles(Profiles profiles) {
        return getEnvironment().acceptsProfiles(profiles);
    }

    /**
     * 解析文本中的 {@code ${...}} 占位符。
     * <p>使用当前 {@link Environment} 中已注册的属性源进行替换。
     * 无法解析的占位符保持原样。</p>
     *
     * @param text 包含占位符的文本
     * @return 解析后的文本
     */
    public static String resolvePlaceholders(String text) {
        return getEnvironment().resolvePlaceholders(text);
    }

    /**
     * 获取指定属性值（字符串形式）。
     *
     * @param property 属性键
     * @return 属性值，不存在时返回 {@code null}
     */
    @Nullable
    public static String getProperty(String property) {
        return getEnvironment().getProperty(property);
    }

    /**
     * 获取指定类型的属性值。
     *
     * @param key        属性键
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 属性值，不存在时返回 {@code null}
     */
    @Nullable
    public static <T> T getProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    /**
     * 获取指定类型的属性值，不存在时返回默认值。
     *
     * @param key          属性键
     * @param targetType   目标类型
     * @param defaultValue 默认值
     * @param <T>          目标类型泛型
     * @return 属性值或默认值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 获取属性值（字符串形式），不存在时返回默认值。
     *
     * @param key          属性键
     * @param defaultValue 默认值
     * @return 属性值或默认值
     */
    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    // ------

    /**
     * 持有降级 {@link StandardEnvironment} 的延迟初始化持有者。
     */
    private static class LazyHolder {
        private static final Environment DEFAULT_ENVIRONMENT = new StandardEnvironment();
    }

}
