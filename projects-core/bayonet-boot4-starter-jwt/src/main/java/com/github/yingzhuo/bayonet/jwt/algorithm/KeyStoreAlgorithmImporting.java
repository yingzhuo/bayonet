package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.security.KeyStore;
import java.security.PrivateKey;

@RequiredArgsConstructor
class KeyStoreAlgorithmImporting implements ImportBeanDefinitionRegistrar {

    private final Environment environment;
    private final ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        var importAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(KeyStoreAlgorithm.class.getName()));

        if (importAttributes == null) {
            return;
        }

        var type = importAttributes.<KeyStoreType>getEnum("type");
        var location = importAttributes.getString("location");
        var storepass = importAttributes.getString("storepass");
        var alias = importAttributes.getString("alias");
        var keypass = importAttributes.getString("keypass");
        var algorithmName = importAttributes.<AlgorithmName>getEnum("algorithmName");
        var primary = importAttributes.getBoolean("primary");

        var target = getAlgorithm(type, location, storepass, alias, keypass, algorithmName);
        var beanDef = (GenericBeanDefinition) BeanDefinitionBuilder.genericBeanDefinition(Algorithm.class, () -> target)
                .setPrimary(primary)
                .setAbstract(false)
                .setLazyInit(false)
                .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                .setRole(AbstractBeanDefinition.ROLE_APPLICATION)
                .getBeanDefinition();

        var beanName = beanNameGenerator.generateBeanName(beanDef, registry);
        registry.registerBeanDefinition(beanName, beanDef);
    }

    private Algorithm getAlgorithm(
            KeyStoreType type,
            String location,
            String storepass,
            String alias,
            String keypass,
            AlgorithmName algorithmName
    ) {
        location = environment.resolvePlaceholders(location);
        storepass = environment.resolvePlaceholders(storepass);
        alias = environment.resolvePlaceholders(alias);
        keypass = environment.resolvePlaceholders(keypass);

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
