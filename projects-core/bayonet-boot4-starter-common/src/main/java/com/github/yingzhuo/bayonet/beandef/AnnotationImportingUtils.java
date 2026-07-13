package com.github.yingzhuo.bayonet.beandef;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationImportingUtils {

    /**
     * 获取指定注解的属性。
     * <p>若目标注解不存在于 {@code metadata} 上，返回空 {@link AnnotationAttributes}（非 {@code null}）。</p>
     *
     * @param metadata      AnnotationMetadata
     * @param annotationType 注解类型
     * @return 注解属性（非 {@code null}）
     */
    public static AnnotationAttributes getAnnotationAttributes(AnnotationMetadata metadata, Class<? extends Annotation> annotationType) {
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
    public static Set<AnnotationAttributes> getAnnotationAttributesSet(
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
