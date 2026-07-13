package com.github.yingzhuo.bayonet.beandef;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationImportingUtilsTest {

    @Mock
    private AnnotationMetadata metadata;

    @Retention(RetentionPolicy.RUNTIME)
    @interface SimpleAnnotation {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(ContainerAnnotation.class)
    @interface RepeatableAnnotation {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ContainerAnnotation {
        RepeatableAnnotation[] value();
    }

    // ============== getAnnotationAttributes ==============

    @Test
    void getAnnotationAttributes_should_return_attributes_when_present() {
        when(metadata.getAnnotationAttributes(SimpleAnnotation.class.getName(), false))
                .thenReturn(Map.of("value", "hello"));

        var result = AnnotationImportingUtils.getAnnotationAttributes(metadata, SimpleAnnotation.class);
        assertThat(result).isNotNull();
        assertThat(result.getString("value")).isEqualTo("hello");
    }

    @Test
    void getAnnotationAttributes_should_return_empty_when_not_present() {
        when(metadata.getAnnotationAttributes(SimpleAnnotation.class.getName(), false))
                .thenReturn(null);

        var result = AnnotationImportingUtils.getAnnotationAttributes(metadata, SimpleAnnotation.class);
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void getAnnotationAttributes_should_throw_when_metadataNull() {
        assertThatThrownBy(() -> AnnotationImportingUtils.getAnnotationAttributes(null, SimpleAnnotation.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAnnotationAttributes_should_throw_when_annotationTypeNull() {
        assertThatThrownBy(() -> AnnotationImportingUtils.getAnnotationAttributes(metadata, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getAnnotationAttributesSet（不含容器注解） ==============

    @Test
    void getAnnotationAttributesSet_withoutContainer_should_return_attributes() {
        when(metadata.getAnnotationAttributes(SimpleAnnotation.class.getName(), false))
                .thenReturn(Map.of("value", "hello"));

        var result = AnnotationImportingUtils.getAnnotationAttributesSet(metadata, SimpleAnnotation.class, null);
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getString("value")).isEqualTo("hello");
    }

    @Test
    void getAnnotationAttributesSet_withoutContainer_should_return_empty_when_not_present() {
        when(metadata.getAnnotationAttributes(SimpleAnnotation.class.getName(), false))
                .thenReturn(null);

        var result = AnnotationImportingUtils.getAnnotationAttributesSet(metadata, SimpleAnnotation.class, null);
        assertThat(result).isEmpty();
    }

    @Test
    void getAnnotationAttributesSet_withoutContainer_should_throw_when_metadataNull() {
        assertThatThrownBy(() ->
                AnnotationImportingUtils.getAnnotationAttributesSet(null, SimpleAnnotation.class, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAnnotationAttributesSet_withoutContainer_should_throw_when_annotationNull() {
        assertThatThrownBy(() ->
                AnnotationImportingUtils.getAnnotationAttributesSet(metadata, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getAnnotationAttributesSet（含容器注解） ==============

    @Test
    void getAnnotationAttributesSet_withContainer_should_return_attributes() {
        var attr1 = AnnotationAttributes.fromMap(Map.of("value", "a"));
        var attr2 = AnnotationAttributes.fromMap(Map.of("value", "b"));

        when(metadata.getMergedRepeatableAnnotationAttributes(
                RepeatableAnnotation.class, ContainerAnnotation.class, false))
                .thenReturn(Set.of(attr1, attr2));

        var result = AnnotationImportingUtils.getAnnotationAttributesSet(
                metadata, RepeatableAnnotation.class, ContainerAnnotation.class);
        assertThat(result).hasSize(2);
    }

    @Test
    void getAnnotationAttributesSet_withContainer_should_return_empty_when_not_present() {
        when(metadata.getMergedRepeatableAnnotationAttributes(
                RepeatableAnnotation.class, ContainerAnnotation.class, false))
                .thenReturn(Set.of());

        var result = AnnotationImportingUtils.getAnnotationAttributesSet(
                metadata, RepeatableAnnotation.class, ContainerAnnotation.class);
        assertThat(result).isEmpty();
    }

    @Test
    void getAnnotationAttributesSet_withContainer_should_throw_when_metadataNull() {
        assertThatThrownBy(() ->
                AnnotationImportingUtils.getAnnotationAttributesSet(
                        null, RepeatableAnnotation.class, ContainerAnnotation.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAnnotationAttributesSet_withContainer_should_throw_when_annotationNull() {
        assertThatThrownBy(() ->
                AnnotationImportingUtils.getAnnotationAttributesSet(metadata, null, ContainerAnnotation.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
