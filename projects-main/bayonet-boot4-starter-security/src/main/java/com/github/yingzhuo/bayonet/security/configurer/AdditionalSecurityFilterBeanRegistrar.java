package com.github.yingzhuo.bayonet.security.configurer;

import com.github.yingzhuo.bayonet.beandef.BeanRegistrarSupport;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;

/**
 * {@link AdditionalSecurityFilter @AdditionalSecurityFilter} 注解的 Bean 定义注册器。
 * <p>实现 {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar ImportBeanDefinitionRegistrar}，
 * 解析 {@code @AdditionalSecurityFilter} 注解并将其转换为 {@link AdditionalFilterConfig} Bean 注册到容器中。</p>
 *
 * @author 应卓
 * @see AdditionalSecurityFilter
 * @see AdditionalFilterConfig
 * @since 4.1.1
 */
class AdditionalSecurityFilterBeanRegistrar extends BeanRegistrarSupport {

    public AdditionalSecurityFilterBeanRegistrar(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {

        var attributesSet = super.getAnnotationAttributesSet(importingClassMetadata,
                AdditionalSecurityFilter.class,
                AdditionalSecurityFilter.List.class
        );

        for (var attributes : attributesSet) {
            var skipIfAnyProfileActivated = attributes.getStringArray("skipIfAnyProfileActivated");

            // 当任一指定 Profile 激活时，跳过此过滤器
            if (skipIfAnyProfileActivated.length > 0) {
                var activeProfiles = List.of(super.environment.getActiveProfiles());
                var shouldSkip = Arrays.stream(skipIfAnyProfileActivated).anyMatch(activeProfiles::contains);
                if (shouldSkip) {
                    continue;
                }
            }

            var filterType = attributes.<Filter>getClass("filterType");
            var positionFilterType = attributes.<Filter>getClass("positionFilterType");
            var hint = attributes.<FilterPositionHint>getEnum("hint");
            var conf = new AdditionalFilterConfig(
                    filterType,
                    positionFilterType,
                    hint
            );

            var beanDef = BeanDefinitionBuilder.genericBeanDefinition(AdditionalFilterConfig.class, () -> conf)
                    .getBeanDefinition();

            var beanName = beanNameGenerator.generateBeanName(beanDef, registry) + "-" + System.identityHashCode(conf);
            registry.registerBeanDefinition(beanName, beanDef);
        }
    }
}
