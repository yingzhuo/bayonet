package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.beandef.AnnotationImportingUtils;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.security.KeyStore;
import java.security.PrivateKey;

class KeyStoreAlgorithmImporting extends AbstractAlgorithmImporting {

    public KeyStoreAlgorithmImporting(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        var importAttributes = AnnotationImportingUtils.getAnnotationAttributes(importingClassMetadata, KeyStoreAlgorithm.class);

        var type = importAttributes.<KeyStoreType>getEnum("type");
        var location = environment.resolvePlaceholders(importAttributes.getString("location"));
        var storepass = environment.resolvePlaceholders(importAttributes.getString("storepass"));
        var alias = environment.resolvePlaceholders(importAttributes.getString("alias"));
        var keypass = environment.resolvePlaceholders(importAttributes.getString("keypass"));
        var algorithmName = importAttributes.<AlgorithmName>getEnum("algorithmName");
        var primary = importAttributes.getBoolean("primary");
        var beanAliases = importAttributes.getStringArray("beanAliases");

        var target = getAlgorithm(type, location, storepass, alias, keypass, algorithmName);
        var beanDef = super.createBeanDefinition(target, primary);
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

        try (var stream = resourceLoader.getResource(location).getInputStream()) {
            var ks = KeyStore.getInstance(type.name());
            ks.load(stream, keypass.toCharArray());

            var publicKey = ks.getCertificate(alias).getPublicKey();
            var privateKey = (PrivateKey) ks.getKey(alias, keypass.toCharArray());

            return AlgorithmFactories.createAlgorithm(algorithmName, publicKey, privateKey);
        } catch (Exception e) {
            throw new BeanDefinitionStoreException(e.getMessage(), e);
        }
    }

}
