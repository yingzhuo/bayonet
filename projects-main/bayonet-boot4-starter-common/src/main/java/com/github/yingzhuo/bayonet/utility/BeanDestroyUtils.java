package com.github.yingzhuo.bayonet.utility;

import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Bean 销毁工具类。
 * <p>从 {@link ApplicationContext} 中销毁并移除指定的 Bean。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ApiStatus.Experimental
public final class BeanDestroyUtils {

    /**
     * 销毁指定的 Bean。
     * <p>仅当 {@link ApplicationContext} 为 {@link ConfigurableApplicationContext} 实例时生效。</p>
     *
     * @param applicationContext Spring 应用上下文
     * @param beanNames          要销毁的 Bean 名称列表
     */
    public static void destroyIfPossible(ApplicationContext applicationContext, String... beanNames) {
        if (!(applicationContext instanceof ConfigurableApplicationContext) || ArrayUtils.isEmpty(beanNames)) {
            return;
        }

        for (var beanName : beanNames) {
            try {
                doDestroy((ConfigurableApplicationContext) applicationContext, beanName);
            } catch (Exception e) {
                log.info("Failed to destroy bean '{}'", beanName, e);
            }
        }
    }

    private static void doDestroy(ConfigurableApplicationContext configurable, String beanName) {
        if (StringUtils.isBlank(beanName)) {
            return;
        }

        // 触发 DisposableBean.destroy() / @PreDestroy / 自定义 destroyMethod
        configurable.getBeanFactory().destroyBean(beanName);

        // 若 BeanDefinition 残留则一并移除
        if (configurable instanceof BeanDefinitionRegistry registry
                && registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName);
        }
    }
}
