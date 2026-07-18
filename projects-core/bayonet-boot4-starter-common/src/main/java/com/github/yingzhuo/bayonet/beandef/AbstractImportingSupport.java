package com.github.yingzhuo.bayonet.beandef;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Set;

abstract class AbstractImportingSupport {

    protected final ResourceLoader resourceLoader;
    protected final ResourcePatternResolver resourcePatternResolver;
    protected final Environment environment;
    protected final BeanFactory beanFactory;
    protected final ClassLoader beanClassLoader;

    /**
     * 构造器
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    protected AbstractImportingSupport(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.notNull(environment, "environment must not be null");
        Assert.notNull(beanFactory, "beanFactory must not be null");
        Assert.notNull(beanClassLoader, "beanClassLoader must not be null");

        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * 获取指定注解的属性
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
     * 获取导入注解的属性集合（可选支持容器注解）
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
}
