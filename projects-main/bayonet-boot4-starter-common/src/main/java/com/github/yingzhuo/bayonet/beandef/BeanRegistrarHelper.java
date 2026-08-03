package com.github.yingzhuo.bayonet.beandef;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * BeanDefinition 注册辅助工具。
 * <p>提供注解属性读取与 Bean 别名注册等静态方法。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanRegistrarHelper {

    /**
     * 获取指定注解的属性，该注解必须存在。
     *
     * @param metadata       注解元数据（非 {@code null}）
     * @param annotationType 注解类型（非 {@code null}）
     * @return 注解属性（非 {@code null}）
     * @throws IllegalArgumentException 若元数据上不存在该注解
     */
    public static AnnotationAttributes getRequiredAnnotationAttributes(
            AnnotatedTypeMetadata metadata,
            Class<? extends Annotation> annotationType) {

        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(annotationType, "annotationType must not be null");

        var annotationName = annotationType.getName();
        var map = metadata.getAnnotationAttributes(annotationName, false);
        if (map == null) {
            throw new IllegalArgumentException("annotation attributes not found: '" + annotationName + "'");
        }
        return AnnotationAttributes.fromMap(map);
    }

    /**
     * 获取指定注解的属性集合，支持可重复注解。
     * <p>当注解不存在时返回空集合；{@code importingContainerAnnotation} 为 {@code null} 时按普通注解处理。</p>
     *
     * @param metadata                     注解元数据（非 {@code null}）
     * @param importingAnnotation          注解类型（非 {@code null}）
     * @param importingContainerAnnotation 可重复注解的容器注解，可为 {@code null}
     * @return 注解属性集合（不可变，非 {@code null}）
     */
    public static Set<AnnotationAttributes> getAnnotationAttributes(
            AnnotatedTypeMetadata metadata,
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

    /**
     * 注册 Bean 别名
     *
     * @param beanName   Bean 名称
     * @param aliasArray 别名数组（可为空数组）
     * @param registry   BeanDefinitionRegistry
     */
    public static void registerBeanAlias(String beanName, String[] aliasArray, BeanDefinitionRegistry registry) {
        Assert.hasText(beanName, "beanName must not be null");
        Assert.notNull(aliasArray, "aliasArray must not be null");
        Assert.noNullElements(aliasArray, "aliasArray must not contain null elements");
        Assert.notNull(registry, "registry must not be null");

        // @formatter:off
        Arrays.stream(aliasArray)
                .filter(StringUtils::hasText)
                .forEach(alias -> registry.registerAlias(beanName, alias));
        // @formatter:on
    }
}
