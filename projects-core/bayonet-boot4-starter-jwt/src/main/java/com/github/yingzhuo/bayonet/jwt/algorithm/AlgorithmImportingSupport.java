package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * JWT Algorithm 注册器抽象支持类。
 * <p>继承自 {@link BeanDefinitionRegistrarSupport}，提供创建 {@link Algorithm} Bean 定义的通用方法。
 * 子类只需关注从注解读取属性和构建具体的 {@link Algorithm} 实例。</p>
 *
 * @see PemAlgorithmImporting
 * @see KeyStoreAlgorithmImporting
 */
public abstract class AlgorithmImportingSupport extends BeanDefinitionRegistrarSupport {

    /**
     * 构造器
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected AlgorithmImportingSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    /**
     * 创建 {@link Algorithm} 的 Bean 定义
     * <p>使用 {@link BeanDefinitionBuilder} 构建单例 Bean 定义，并根据 {@code primary} 参数
     * 设置是否为 {@code @Primary} Bean。默认作用域为单例，非抽象，非延迟初始化。</p>
     *
     * @param algorithm Algorithm 实例
     * @param primary   是否作为主候选 Bean
     * @return Bean 定义
     */
    protected final BeanDefinition createBeanDefinition(Algorithm algorithm, boolean primary) {
        return BeanDefinitionBuilder.genericBeanDefinition(Algorithm.class, () -> algorithm)
                .setPrimary(primary)
                .getBeanDefinition();
    }

}
