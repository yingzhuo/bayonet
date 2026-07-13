package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

@RequiredArgsConstructor
class PemAlgorithmImporting implements ImportBeanDefinitionRegistrar {

    private final ResourceLoader resourceLoader;
    private final Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        var importingAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(PemAlgorithm.class.getName()));

        if (importingAttributes == null) {
            return;
        }

        var location = environment.resolvePlaceholders(importingAttributes.getString("location"));
        var keypass = environment.resolvePlaceholders(importingAttributes.getString("keypass"));
        var algorithmName = importingAttributes.<AlgorithmName>getEnum("algorithmName");
        var primary = importingAttributes.getBoolean("primary");

        var target = getAlgorithm(location, keypass, algorithmName);
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

    private Algorithm getAlgorithm(String location, String keypass, AlgorithmName algorithmName) {

        try (var stream = resourceLoader.getResource(location).getInputStream()) {
            var pemContent = PemContent.load(stream);

            var certificates = pemContent.getCertificates();
            Assert.notEmpty(certificates, "cannot load X509Certificate");

            var publicKey = certificates.get(0).getPublicKey();
            var privateKey = pemContent.getPrivateKey(keypass);
            Assert.notNull(privateKey, "cannot load private key");

            return AlgorithmFactories.createAlgorithm(algorithmName, publicKey, privateKey);

        } catch (Exception e) {
            throw new BeanDefinitionStoreException(e.getMessage(), e);
        }
    }

}
