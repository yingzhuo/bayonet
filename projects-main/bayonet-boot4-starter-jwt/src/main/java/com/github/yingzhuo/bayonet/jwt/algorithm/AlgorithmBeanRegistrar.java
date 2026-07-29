package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

sealed abstract class AlgorithmBeanRegistrar extends BeanDefinitionRegistrarSupport
        permits KeyStoreAlgorithmBeanRegistrar, PemAlgorithmBeanRegistrar {

    protected AlgorithmBeanRegistrar(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    protected final BeanDefinition createBeanDefinition(Algorithm algorithm, boolean primary) {
        return BeanDefinitionBuilder.genericBeanDefinition(Algorithm.class, () -> algorithm)
                .setPrimary(primary)
                .getBeanDefinition();
    }
}
