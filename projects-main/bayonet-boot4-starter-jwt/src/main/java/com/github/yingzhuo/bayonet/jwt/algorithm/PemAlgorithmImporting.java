package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyBundleFactories;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * @author 应卓
 * @since 4.1.0
 */
@ApiStatus.Experimental
class PemAlgorithmImporting extends AlgorithmImportingSupport {

    public PemAlgorithmImporting(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        var importingAttributes = getAnnotationAttributes(importingClassMetadata, PemAlgorithm.class);

        var location = environment.resolvePlaceholders(importingAttributes.getString("location"));
        var keypass = environment.resolvePlaceholders(importingAttributes.getString("keypass"));
        var algorithmName = importingAttributes.<AlgorithmName>getEnum("algorithmName");
        var primary = importingAttributes.getBoolean("primary");
        var beanAliases = importingAttributes.getStringArray("beanAliases");

        var target = getAlgorithm(location, keypass, algorithmName);
        var beanDef = super.createBeanDefinition(target, primary);
        var beanName = beanNameGenerator.generateBeanName(beanDef, registry);
        registry.registerBeanDefinition(beanName, beanDef);

        super.registerBeanAlias(beanAliases, beanName, registry);
    }

    private Algorithm getAlgorithm(String location, String keypass, AlgorithmName algorithmName) {
        if (!StringUtils.hasText(keypass)) {
            keypass = null;
        }

        var keyBundle = KeyBundleFactories.loadFromPem(location, keypass);
        return AlgorithmFactories.createAlgorithm(algorithmName, keyBundle.getPublicKey(), keyBundle.getPrivateKey());
    }

}
