package com.github.yingzhuo.bayonet.beandef;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * {@link ImportBeanDefinitionRegistrar} 抽象支持类。
 * <p>提供导入类的 Class、Package、Annotation 等元数据获取工具方法，
 * 强制子类通过构造函数注入 {@link ResourceLoader}、{@link Environment}、
 * {@link BeanFactory}、{@link ClassLoader} 四个依赖。</p>
 *
 * <pre>{@code
 * public class MyRegistrar extends AbstractImportBeanDefinitionRegistrarSupport {
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
 */
public abstract class AbstractImportBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar {

    protected final ResourceLoader resourceLoader;
    protected final Environment environment;
    protected final BeanFactory beanFactory;
    protected final ClassLoader beanClassLoader;

    /**
     * 构造器。
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected AbstractImportBeanDefinitionRegistrarSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.notNull(environment, "environment must not be null");
        Assert.notNull(beanFactory, "beanFactory must not be null");
        Assert.notNull(beanClassLoader, "beanClassLoader must not be null");

        this.resourceLoader = resourceLoader;
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * 注册 Bean 定义（2 参数版本，已弃用）。
     * <p>标记为 {@code final} 禁止子类覆盖，统一使用 3 参数版本。</p>
     */
    @Override
    public final void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }

}
