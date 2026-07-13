package com.github.yingzhuo.bayonet.beandef;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Set;

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
 */
public abstract class BeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar {

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
    protected BeanDefinitionRegistrarSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
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

    // ------

    /**
     * 获取指定注解的属性。
     * <p>若目标注解不存在于 {@code metadata} 上，返回空 {@link AnnotationAttributes}（非 {@code null}）。</p>
     *
     * @param metadata       AnnotationMetadata
     * @param annotationType 注解类型
     * @return 注解属性（非 {@code null}）
     */
    protected final AnnotationAttributes getAnnotationAttributes(AnnotationMetadata metadata, Class<? extends Annotation> annotationType) {
        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(annotationType, "annotationType must not be null");

        var attrMap = metadata.getAnnotationAttributes(annotationType.getName(), false);
        var attributes = AnnotationAttributes.fromMap(attrMap);
        return attributes != null ? attributes : new AnnotationAttributes();
    }

    /**
     * 获取导入注解的属性集合（可选支持容器注解）。
     *
     * @param metadata                     AnnotationMetadata
     * @param importingAnnotation          导入注解类型
     * @param importingContainerAnnotation 容器注解类型（{@code @Repeatable}），可为 {@code null}
     * @return 注解属性集合
     */
    protected final Set<AnnotationAttributes> getAnnotationAttributesSet(
            AnnotationMetadata metadata,
            Class<? extends Annotation> importingAnnotation,
            @Nullable Class<? extends Annotation> importingContainerAnnotation) {

        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(importingAnnotation, "importingAnnotation must not be null");

        if (importingContainerAnnotation == null) {
            var attrMap = metadata.getAnnotationAttributes(importingAnnotation.getName(), false);
            if (attrMap == null) {
                return Set.of();
            }
            var attributes = AnnotationAttributes.fromMap(attrMap);
            return attributes == null ? Set.of() : Set.of(attributes);
        }

        return metadata.getMergedRepeatableAnnotationAttributes(importingAnnotation, importingContainerAnnotation, false);
    }

    // ------

    /**
     * 注册 Bean 别名。
     * <p>遍历别名数组，对每个别名执行占位符解析后注册到 {@code registry} 中。
     * 占位符使用 {@link org.springframework.core.env.Environment#resolvePlaceholders} 解析。</p>
     *
     * @param aliasArray 别名数组（可为空数组）
     * @param beanName   Bean 名称
     * @param registry   BeanDefinitionRegistry
     */
    protected final void registerBeanAlias(String[] aliasArray, String beanName, BeanDefinitionRegistry registry) {
        Assert.notNull(aliasArray, "aliasArray must not be null");
        Assert.hasText(beanName, "beanName must not be null");
        Assert.notNull(registry, "registry must not be null");

        for (var alias : aliasArray) {
            alias = environment.resolvePlaceholders(alias);
            registry.registerAlias(beanName, alias);
        }
    }

}
