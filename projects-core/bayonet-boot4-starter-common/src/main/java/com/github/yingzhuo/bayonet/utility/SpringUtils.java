package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Spring 应用上下文工具类。
 * <p>在 {@link ApplicationReadyEvent} 触发后持有 {@link ApplicationContext} 引用，
 * 提供便捷的 Bean 获取方法。</p>
 *
 * <pre>{@code
 * var bean = SpringUtils.getBean(SomeService.class);
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpringUtils {

    /**
     * 获取当前 {@link ApplicationContext}。
     *
     * @return ApplicationContext
     * @throws IllegalStateException 若 ApplicationContext 尚未初始化
     */
    public static ApplicationContext getApplicationContext() {
        if (Hook.applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized");
        }
        return Hook.applicationContext;
    }

    /**
     * 按类型获取 Bean。
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 按名称和类型获取 Bean。
     *
     * @param beanName Bean 名称
     * @param clazz    Bean 类型
     * @param <T>      Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return getApplicationContext().getBean(beanName, clazz);
    }

    /**
     * 按类型获取 {@link ObjectProvider}。
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return ObjectProvider
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        return getApplicationContext().getBeanProvider(clazz, true);
    }

    /**
     * 按类型获取 {@link ObjectProvider}，可指定是否允许提前初始化。
     *
     * @param clazz          Bean 类型
     * @param allowEagerInit 是否允许提前初始化
     * @param <T>            Bean 类型
     * @return ObjectProvider
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz, boolean allowEagerInit) {
        return getApplicationContext().getBeanProvider(clazz, allowEagerInit);
    }

    /**
     * 按类型获取所有 Bean 实例。
     *
     * @param clazz Bean 类型（非 {@code null}）
     * @param <T>   Bean 类型
     * @return 该类型的所有 Bean 实例集合（非 {@code null}，可能为空）
     */
    public static <T> Collection<T> getBeans(Class<T> clazz) {
        Assert.notNull(clazz, "clazz must not be null");
        return getApplicationContext().getBeansOfType(clazz).values();
    }

    // ------

    public static SpringApplication getSpringApplication() {
        if (Hook.springApplication == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized");
        }
        return Hook.springApplication;
    }

    // ------

    public static class Hook implements ApplicationListener<ApplicationReadyEvent>, EnvironmentPostProcessor, Ordered {
        static @Nullable ApplicationContext applicationContext;
        static @Nullable SpringApplication springApplication;

        public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
            // 在 applicationContext 容器被初始化之前本方法就会被调用
            Hook.springApplication = application;
        }

        public void onApplicationEvent(ApplicationReadyEvent event) {
            Hook.applicationContext = event.getApplicationContext();
        }

        public int getOrder() {
            return HIGHEST_PRECEDENCE;
        }
    }

}
