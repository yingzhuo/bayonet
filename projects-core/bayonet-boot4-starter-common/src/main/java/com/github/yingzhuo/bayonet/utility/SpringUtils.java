package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

/**
 * Spring 应用上下文工具类。
 * <p>在 {@link ApplicationReadyEvent} 触发后持有 {@link ApplicationContext} 引用，
 * 提供便捷的 Bean 获取方法。</p>
 *
 * <pre>{@code
 * var bean = SpringUtils.getBean(SomeService.class);
 * }</pre>
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

    private static class Hook implements ApplicationListener<ApplicationReadyEvent> {
        static @Nullable ApplicationContext applicationContext;

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            Hook.applicationContext = event.getApplicationContext();
        }
    }

}
