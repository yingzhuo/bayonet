package com.github.yingzhuo.bayonet.beandef;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Set;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * BeanDefinition 注册辅助工具
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanRegistrarHelper {

    /**
     * 获取指定注解的属性，该注解必须存在
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
        var map = metadata.getAnnotationAttributes(annotationName);
        if (map == null) {
            throw new IllegalArgumentException("annotation attributes not found: '" + annotationName + "'");
        }
        return AnnotationAttributes.fromMap(map);
    }

    /**
     * 获取指定注解的属性集合，支持可重复注解
     * <p>当注解不存在时返回空集合；{@code importingContainerAnnotation} 为 {@code null} 时按普通注解处理。</p>
     *
     * @param metadata       注解元数据（非 {@code null}）
     * @param annotationType 注解类型（非 {@code null}）
     * @param containerType  可重复注解的容器注解，可为 {@code null}
     * @return 注解属性集合（不可变，非 {@code null}）
     */
    public static Set<AnnotationAttributes> getAnnotationAttributesSet(
            AnnotatedTypeMetadata metadata,
            Class<? extends Annotation> annotationType,
            @Nullable Class<? extends Annotation> containerType) {

        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(annotationType, "importingAnnotation must not be null");

        if (containerType == null) {
            var attrMap = metadata.getAnnotationAttributes(annotationType.getName());
            if (attrMap == null) {
                return Set.of();
            }
            var attributes = AnnotationAttributes.fromMap(attrMap);
            return attributes == null ? Set.of() : Set.of(attributes);
        }

        return metadata.getMergedRepeatableAnnotationAttributes(
                annotationType,
                containerType,
                false,
                true
        );
    }

    // ------

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
        Assert.notNull(registry, "registry must not be null");

        for (var alias : aliasArray) {
            if (StringUtils.hasText(alias)) {
                registry.registerAlias(beanName, alias);
            }
        }
    }

    // ------

    /**
     * 创建 BeanDefinition（非主 Bean、非懒加载、单例作用域）。
     *
     * @param beanType Bean 类型（非 {@code null}）
     * @param instance 已实例化的 Bean 实例（非 {@code null}）
     * @param <B>      Bean 类型
     * @return BeanDefinition（非 {@code null}）
     */
    public static <B> AbstractBeanDefinition createBeanDefinition(Class<B> beanType, B instance) {
        return createBeanDefinition(beanType, instance, false, false);
    }

    /**
     * 创建 BeanDefinition（单例作用域）。
     *
     * @param beanType Bean 类型（非 {@code null}）
     * @param instance 已实例化的 Bean 实例（非 {@code null}）
     * @param primary  是否为主 Bean
     * @param lazyInit 是否懒加载
     * @param <B>      Bean 类型
     * @return BeanDefinition（非 {@code null}）
     */
    public static <B> AbstractBeanDefinition createBeanDefinition(Class<B> beanType, B instance, boolean primary, boolean lazyInit) {
        return createBeanDefinition(beanType, instance, primary, lazyInit, SCOPE_SINGLETON);
    }

    /**
     * 创建 BeanDefinition。
     *
     * @param beanType Bean 类型（非 {@code null}）
     * @param instance 已实例化的 Bean 实例（非 {@code null}）
     * @param primary  是否为主 Bean
     * @param lazyInit 是否懒加载
     * @param scope    作用域，仅支持 {@code singleton} 或 {@code prototype}（大小写不敏感）
     * @param <B>      Bean 类型
     * @return BeanDefinition（非 {@code null}）
     * @throws IllegalArgumentException 若 {@code scope} 非法
     */
    public static <B> AbstractBeanDefinition createBeanDefinition(Class<B> beanType, B instance, boolean primary, boolean lazyInit, String scope) {
        Assert.notNull(beanType, "beanType must not be null");
        Assert.notNull(instance, "instance must not be null");

        return BeanDefinitionBuilder.genericBeanDefinition(beanType, () -> instance)
                .setRole(BeanDefinition.ROLE_APPLICATION)
                .setAbstract(false)
                .setLazyInit(lazyInit)
                .setPrimary(primary)
                .setScope(checkScope(scope))
                .getBeanDefinition();
    }

    // ------

    private static String checkScope(String scope) {
        Assert.notNull(scope, "scope must not be null");
        return switch (scope.toLowerCase(Locale.ROOT)) {
            case "singleton" -> SCOPE_SINGLETON;
            case "prototype" -> SCOPE_PROTOTYPE;
            default -> throw new IllegalArgumentException("scope must be either 'singleton' or 'prototype'");
        };
    }

}
