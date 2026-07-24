package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyBundleFactories;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

@ApiStatus.Experimental
class KeyStoreAlgorithmImporting extends AlgorithmImportingSupport {

    public KeyStoreAlgorithmImporting(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        var importAttributes = getAnnotationAttributes(importingClassMetadata, KeyStoreAlgorithm.class);

        var type = importAttributes.<KeyStoreType>getEnum("type");
        var location = environment.resolvePlaceholders(importAttributes.getString("location"));
        var storepass = environment.resolvePlaceholders(importAttributes.getString("storepass"));
        var alias = environment.resolvePlaceholders(importAttributes.getString("alias"));
        var keypass = environment.resolvePlaceholders(importAttributes.getString("keypass"));
        var algorithmName = importAttributes.<AlgorithmName>getEnum("algorithmName");
        var primary = importAttributes.getBoolean("primary");
        var beanAliases = importAttributes.getStringArray("beanAliases");

        var target = getAlgorithm(type, location, storepass, alias, keypass, algorithmName);
        var beanDef = createBeanDefinition(target, primary);
        var beanName = beanNameGenerator.generateBeanName(beanDef, registry);
        registry.registerBeanDefinition(beanName, beanDef);

        super.registerBeanAlias(beanAliases, beanName, registry);
    }

    private Algorithm getAlgorithm(
            KeyStoreType type,
            String location,
            String storepass,
            String alias,
            String keypass,
            AlgorithmName algorithmName
    ) {
        if (!StringUtils.hasText(keypass)) {
            keypass = storepass;
        }

        var keyBundle = KeyBundleFactories.loadFromStore(location, type, storepass, alias, keypass);
        return AlgorithmFactories.createAlgorithm(algorithmName, keyBundle.getPublicKey(), keyBundle.getPrivateKey());
    }

}
