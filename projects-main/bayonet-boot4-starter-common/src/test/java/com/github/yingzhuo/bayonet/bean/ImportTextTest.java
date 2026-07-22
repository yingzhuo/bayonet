package com.github.yingzhuo.bayonet.bean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportTextTest {

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    Environment environment;

    @Mock
    BeanFactory beanFactory;

    @Mock
    ClassLoader classLoader;

    @Mock
    BeanDefinitionRegistry registry;

    @Mock
    AnnotationMetadata metadata;

    private ImportTextImporting createImporting() {
        return new ImportTextImporting(resourceLoader, environment, beanFactory, classLoader);
    }

    private AnnotationAttributes createAttrs(String beanName, String location, boolean primary, String[] aliases) {
        return createAttrs(beanName, location, primary, aliases, false, false);
    }

    private AnnotationAttributes createAttrs(String beanName, String location, boolean primary, String[] aliases, boolean trim, boolean trimEachLine) {
        var attrs = new AnnotationAttributes();
        attrs.put("beanName", beanName);
        attrs.put("location", location);
        attrs.put("primary", primary);
        attrs.put("aliases", aliases);
        attrs.put("trim", trim);
        attrs.put("trimEachLine", trimEachLine);
        return attrs;
    }

    private void mockResource(String location, String content) throws Exception {
        var resource = mock(Resource.class);
        when(resourceLoader.getResource(location)).thenReturn(resource);
        lenient().doReturn(content).when(resource).getContentAsString(StandardCharsets.UTF_8);
        lenient().when(environment.resolvePlaceholders(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ============== 基本注册 ==============

    @Test
    void should_register_bean() throws Exception {
        mockResource("classpath:test.txt", "hello");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("myBean", "classpath:test.txt", false, new String[0])));

        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("myBean"), any());
    }

    // ============== 别名 ==============

    @Test
    void should_register_bean_with_aliases() throws Exception {
        mockResource("classpath:test.txt", "hello");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("myBean", "classpath:test.txt", false, new String[]{"alias1", "alias2"})));

        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("myBean"), any());
        verify(registry).registerAlias("myBean", "alias1");
        verify(registry).registerAlias("myBean", "alias2");
    }

    // ============== primary ==============

    @Test
    void should_register_primary_bean() throws Exception {
        mockResource("classpath:test.txt", "content");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("primaryBean", "classpath:test.txt", true, new String[0])));

        var captor = ArgumentCaptor.forClass(BeanDefinition.class);
        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("primaryBean"), captor.capture());
        assertThat(captor.getValue().isPrimary()).isTrue();
    }

    // ============== 参数验证 ==============

    @Test
    void should_throw_when_location_is_empty() {
        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("myBean", "", false, new String[0])));

        assertThatThrownBy(() -> createImporting().registerBeanDefinitions(metadata, registry, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("location");
    }

    @Test
    void should_register_with_generatedName_when_beanName_is_empty() throws Exception {
        mockResource("classpath:test.txt", "hello");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("", "classpath:test.txt", false, new String[0])));

        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(anyString(), any());
    }

    // ============== 可重复注解 ==============

    @Test
    void should_register_multiple_beans_with_repeatable() throws Exception {
        var attr1 = createAttrs("bean1", "classpath:file1.txt", false, new String[0]);
        var attr2 = createAttrs("bean2", "classpath:file2.txt", false, new String[0]);
        mockResource("classpath:file1.txt", "content1");
        mockResource("classpath:file2.txt", "content2");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(attr1, attr2));

        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("bean1"), any());
        verify(registry).registerBeanDefinition(eq("bean2"), any());
    }

    // ============== 空注解 ==============

    @Test
    void should_do_nothing_when_no_annotation() {
        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of());

        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry, never()).registerBeanDefinition(any(), any());
    }

    // ============== trim ==============

    @Test
    void should_trim_content() throws Exception {
        mockResource("classpath:test.txt", "  hello world  ");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("trimBean", "classpath:test.txt", false, new String[0], true, false)));

        var captor = ArgumentCaptor.forClass(BeanDefinition.class);
        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("trimBean"), captor.capture());
        var supplier = ((AbstractBeanDefinition) captor.getValue()).getInstanceSupplier();
        assertThat(((Supplier<?>) supplier).get()).isEqualTo("hello world");
    }

    // ============== trimEachLine ==============

    @Test
    void should_trim_each_line() throws Exception {
        mockResource("classpath:test.txt", "  a\n  b  \n  c  ");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("lineBean", "classpath:test.txt", false, new String[0], false, true)));

        var captor = ArgumentCaptor.forClass(BeanDefinition.class);
        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("lineBean"), captor.capture());
        var supplier = ((AbstractBeanDefinition) captor.getValue()).getInstanceSupplier();
        assertThat(((Supplier<?>) supplier).get()).isEqualTo("a\nb\nc");
    }

    @Test
    void should_handle_crlf_with_trimEachLine() throws Exception {
        mockResource("classpath:test.txt", "  a\r\n  b  \r\n  c  ");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("crlfBean", "classpath:test.txt", false, new String[0], false, true)));

        var captor = ArgumentCaptor.forClass(BeanDefinition.class);
        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("crlfBean"), captor.capture());
        var supplier = ((AbstractBeanDefinition) captor.getValue()).getInstanceSupplier();
        assertThat(((Supplier<?>) supplier).get()).isEqualTo("a\nb\nc");
    }

    // ============== trim + trimEachLine ==============

    @Test
    void should_trim_then_trimEachLine() throws Exception {
        mockResource("classpath:test.txt", "  hello\n  world  \n");

        when(metadata.getMergedRepeatableAnnotationAttributes(ImportText.class, ImportText.List.class, false))
                .thenReturn(Set.of(createAttrs("bothBean", "classpath:test.txt", false, new String[0], true, true)));

        var captor = ArgumentCaptor.forClass(BeanDefinition.class);
        createImporting().registerBeanDefinitions(metadata, registry, null);

        verify(registry).registerBeanDefinition(eq("bothBean"), captor.capture());
        var supplier = ((AbstractBeanDefinition) captor.getValue()).getInstanceSupplier();
        assertThat(((Supplier<?>) supplier).get()).isEqualTo("hello\nworld");
    }

}
