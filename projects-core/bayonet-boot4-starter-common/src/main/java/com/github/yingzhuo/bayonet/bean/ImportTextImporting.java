package com.github.yingzhuo.bayonet.bean;

import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@ApiStatus.Experimental
@Deprecated
class ImportTextImporting extends BeanDefinitionRegistrarSupport {

    public ImportTextImporting(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator __) {
        var annotationAttributesSet =
                getAnnotationAttributesSet(importingClassMetadata, ImportText.class, ImportText.List.class);

        if (CollectionUtils.isEmpty(annotationAttributesSet)) {
            return;
        }

        for (var annotationAttribute : annotationAttributesSet) {
            var beanName = annotationAttribute.getString("beanName");
            var location = annotationAttribute.getString("location");
            var primary = annotationAttribute.getBoolean("primary");
            var aliases = annotationAttribute.getStringArray("aliases");
            var trim = annotationAttribute.getBoolean("trim");
            var trimEachLine = annotationAttribute.getBoolean("trimEachLine");

            if (!StringUtils.hasText(location)) {
                throw new IllegalArgumentException("location/value must not be empty");
            }

            var text = getText(location, trim, trimEachLine);

            var beanDef = BeanDefinitionBuilder.genericBeanDefinition(String.class, () -> text)
                    .setPrimary(primary)
                    .getBeanDefinition();

            if (!StringUtils.hasText(beanName)) {
                beanName = "textBean_" + System.identityHashCode(text);
            }

            registry.registerBeanDefinition(beanName, beanDef);
            registerBeanAlias(aliases, beanName, registry);
        }
    }

    private String getText(String location, boolean trim, boolean trimEachLine) {
        try {
            var text = resourceLoader.getResource(location).getContentAsString(StandardCharsets.UTF_8);
            if (trim) {
                text = text.strip();
            }
            if (trimEachLine) {
                text = text.lines()
                        .map(String::trim)
                        .collect(Collectors.joining("\n"));
            }
            return environment.resolvePlaceholders(text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
