package com.github.yingzhuo.bayonet.bean;

import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
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

            if (!StringUtils.hasText(location)) {
                throw new IllegalArgumentException("location must not be empty");
            }

            if (!StringUtils.hasText(beanName)) {
                throw new IllegalArgumentException("beanName must not be empty");
            }

            var text = getText(location);

            var beanDef = BeanDefinitionBuilder.genericBeanDefinition(String.class, () -> text)
                    .setPrimary(primary)
                    .getBeanDefinition();

            registry.registerBeanDefinition(beanName, beanDef);
            registerBeanAlias(aliases, beanName, registry);
        }
    }

    private String getText(String location) {
        try {
            var text = resourceLoader.getResource(location).getContentAsString(StandardCharsets.UTF_8);
            return environment.resolvePlaceholders(text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
