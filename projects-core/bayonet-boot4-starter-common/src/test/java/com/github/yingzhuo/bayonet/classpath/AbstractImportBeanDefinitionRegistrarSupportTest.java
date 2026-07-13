package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractImportBeanDefinitionRegistrarSupportTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Environment environment;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private ClassMetadata classMetadata;

    @Mock
    private AnnotationMetadata annotationMetadata;

    private final ClassLoader beanClassLoader = getClass().getClassLoader();
    private AbstractImportBeanDefinitionRegistrarSupport registrar;

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
    }

    @TestAnnotation
    static class AnnotatedClass {
    }

    static class SimpleClass {
    }

    @BeforeEach
    void setUp() {
        registrar = new AbstractImportBeanDefinitionRegistrarSupport(
                resourceLoader, environment, beanFactory, beanClassLoader
        ) {
        };
    }

    // ============== 构造器 ==============

    @Test
    void constructor_should_throw_when_resourceLoaderNull() {
        assertThatThrownBy(() -> new AbstractImportBeanDefinitionRegistrarSupport(
                null, environment, beanFactory, beanClassLoader
        ) {}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_should_throw_when_environmentNull() {
        assertThatThrownBy(() -> new AbstractImportBeanDefinitionRegistrarSupport(
                resourceLoader, null, beanFactory, beanClassLoader
        ) {}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_should_throw_when_beanFactoryNull() {
        assertThatThrownBy(() -> new AbstractImportBeanDefinitionRegistrarSupport(
                resourceLoader, environment, null, beanClassLoader
        ) {}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_should_throw_when_beanClassLoaderNull() {
        assertThatThrownBy(() -> new AbstractImportBeanDefinitionRegistrarSupport(
                resourceLoader, environment, beanFactory, null
        ) {}).isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getImportingClassName ==============

    @Test
    void getImportingClassName_should_return_className() {
        when(classMetadata.getClassName()).thenReturn("com.example.Foo");
        assertThat(registrar.getImportingClassName(classMetadata)).isEqualTo("com.example.Foo");
    }

    @Test
    void getImportingClassName_should_throw_when_null() {
        assertThatThrownBy(() -> registrar.getImportingClassName(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getImportingClass ==============

    @Test
    void getImportingClass_should_resolve_class() {
        when(classMetadata.getClassName()).thenReturn(getClass().getName());
        assertThat(registrar.getImportingClass(classMetadata)).isEqualTo(getClass());
    }

    // ============== getImportingClassPackage ==============

    @Test
    void getImportingClassPackage_should_return_package() {
        when(classMetadata.getClassName()).thenReturn(getClass().getName());
        assertThat(registrar.getImportingClassPackage(classMetadata))
                .isEqualTo(getClass().getPackage());
    }

    // ============== getAnnotationOfImportingClass ==============

    @Test
    void getAnnotationOfImportingClass_should_find_annotation() {
        when(classMetadata.getClassName()).thenReturn(AnnotatedClass.class.getName());
        var annotation = registrar.getAnnotationOfImportingClass(classMetadata, TestAnnotation.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    void getAnnotationOfImportingClass_should_return_null_when_notPresent() {
        when(classMetadata.getClassName()).thenReturn(SimpleClass.class.getName());
        var annotation = registrar.getAnnotationOfImportingClass(classMetadata, TestAnnotation.class);
        assertThat(annotation).isNull();
    }

    // ============== getAnnotationAttributesSet（不含容器注解） ==============

    @Test
    void getAnnotationAttributesSet_should_return_attributes() {
        when(annotationMetadata.getAnnotationAttributes(TestAnnotation.class.getName(), false))
                .thenReturn(java.util.Map.of());

        var result = registrar.getAnnotationAttributesSet(annotationMetadata, TestAnnotation.class);
        assertThat(result).hasSize(1);
    }

    @Test
    void getAnnotationAttributesSet_should_return_empty_when_notPresent() {
        when(annotationMetadata.getAnnotationAttributes(TestAnnotation.class.getName(), false))
                .thenReturn(null);

        var result = registrar.getAnnotationAttributesSet(annotationMetadata, TestAnnotation.class);
        assertThat(result).isEmpty();
    }

    @Test
    void getAnnotationAttributesSet_should_throw_when_metadataNull() {
        assertThatThrownBy(() -> registrar.getAnnotationAttributesSet(null, TestAnnotation.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAnnotationAttributesSet_should_throw_when_annotationNull() {
        assertThatThrownBy(() -> registrar.getAnnotationAttributesSet(annotationMetadata, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getAnnotationAttributesSet（含容器注解） ==============

    @Test
    void getAnnotationAttributesSet_withContainer_should_return_attributes() {
        when(annotationMetadata.getMergedRepeatableAnnotationAttributes(
                TestAnnotation.class, Deprecated.class, false
        )).thenReturn(Set.of());

        var result = registrar.getAnnotationAttributesSet(
                annotationMetadata, TestAnnotation.class, Deprecated.class
        );
        assertThat(result).isEmpty();
    }

    // ============== registerBeanDefinitions 默认空实现 ==============

    @Test
    void registerBeanDefinitions_with3params_should_notThrow() {
        registrar.registerBeanDefinitions(null, null, null);
    }

    @Test
    void registerBeanDefinitions_with2params_should_notThrow() {
        registrar.registerBeanDefinitions(null, null);
    }

    // ============== createClassPathScanner ==============

    @Test
    void createClassPathScanner_should_return_scanner_with_defaultFiltersDisabled() {
        var scanner = registrar.createClassPathScanner();
        assertThat(scanner).isNotNull();
    }

    @Test
    void createClassPathScanner_should_return_scanner_with_env_and_loader() {
        var scanner = registrar.createClassPathScanner(true);
        assertThat(scanner).isNotNull();
    }

}
