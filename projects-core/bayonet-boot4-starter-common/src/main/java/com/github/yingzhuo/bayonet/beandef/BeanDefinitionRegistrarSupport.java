package com.github.yingzhuo.bayonet.beandef;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * {@link ImportBeanDefinitionRegistrar} 抽象支持类。
 * <p>提供导入类的 Class、Package、Annotation 等元数据获取工具方法，
 * 强制子类通过构造函数注入 {@link ResourceLoader}、{@link Environment}、
 * {@link BeanFactory}、{@link ClassLoader} 四个依赖。</p>
 *
 * <pre>{@code
 * public class MyRegistrar extends BeanDefinitionRegistrarSupport {
 *     public MyRegistrar(ResourceLoader resourceLoader, Environment environment,
 *                        BeanFactory beanFactory, ClassLoader beanClassLoader) {
 *         super(resourceLoader, environment, beanFactory, beanClassLoader);
 *     }
 *
 *     @Override
 *     public void registerBeanDefinitions(AnnotationMetadata metadata,
 *             BeanDefinitionRegistry registry, BeanNameGenerator generator) {
 *         // 实现注册逻辑
 *     }
 * }
 * }</pre>
 * @author 应卓
 */
public abstract class BeanDefinitionRegistrarSupport extends AbstractImportingSupport implements ImportBeanDefinitionRegistrar {

    /**
     * 构造器
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected BeanDefinitionRegistrarSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    /**
     * 注册 Bean 定义（2 参数版本，已弃用）
     */
    public final void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }

    // ------

    /**
     * 注册 Bean 别名
     *
     * @param aliasArray 别名数组（可为空数组）
     * @param beanName   Bean 名称
     * @param registry   BeanDefinitionRegistry
     */
    protected final void registerBeanAlias(String[] aliasArray, String beanName, BeanDefinitionRegistry registry) {
        Assert.notNull(aliasArray, "aliasArray must not be null");
        Assert.hasText(beanName, "beanName must not be null");
        Assert.notNull(registry, "registry must not be null");

        Arrays.stream(aliasArray).filter(StringUtils::hasText).forEach(alias -> {
            registry.registerAlias(beanName, alias);
        });
    }

}
