package com.github.yingzhuo.bayonet.webcli.annotation;

import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
import com.github.yingzhuo.bayonet.webcli.factory.JdkClientHttpRequestFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.time.Duration;

/**
 * 用于注册信任所有证书的 {@link ClientHttpRequestFactory} Bean 的 {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar ImportBeanDefinitionRegistrar}。
 *
 * <p>配合 {@link ImportUnsafeClientHttpRequestFactory @ImportUnsafeClientHttpRequestFactory} 注解使用，
 * 创建一个 {@link JdkClientHttpRequestFactoryBean} 实例并配置为信任所有证书，
 * 然后将生成的 {@link ClientHttpRequestFactory} 注册为名为 {@value #BEAN_NAME} 的 Bean。</p>
 *
 * @author 应卓
 * @see ImportUnsafeClientHttpRequestFactory
 * @see UnsafeClientHttpRequestFactory
 * @since 4.1.0
 */
class ImportUnsafeClientHttpRequestFactoryImporting extends BeanDefinitionRegistrarSupport {

    /**
     * 注册的 Bean 名称。
     */
    public static final String BEAN_NAME = "unsafeClientHttpRequestFactory";

    /**
     * 构造器。
     *
     * @param resourceLoader  ResourceLoader
     * @param environment     Environment
     * @param beanFactory     BeanFactory
     * @param beanClassLoader ClassLoader
     */
    public ImportUnsafeClientHttpRequestFactoryImporting(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
        super(resourceLoader, environment, beanFactory, beanClassLoader);
    }

    /**
     * 注册 Bean 定义。
     *
     * <p>从注解属性中读取连接超时和读取超时，创建并配置 {@link JdkClientHttpRequestFactoryBean}，
     * 然后将生成的 {@link ClientHttpRequestFactory} 注册到 Spring 容器。</p>
     *
     * <p>若容器中已存在同名 Bean，则跳过注册以避免重复覆盖。</p>
     *
     * @param importingClassMetadata 导入类的注解元数据
     * @param registry               Bean 定义注册表
     * @param __                     Bean 名称生成器（未使用）
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator __) {
        if (registry.containsBeanDefinition(BEAN_NAME)) {
            return;
        }

        var annotationAttributes = getAnnotationAttributes(importingClassMetadata, ImportUnsafeClientHttpRequestFactory.class);

        var connectTimeout = Duration.ofMillis(annotationAttributes.<Long>getNumber("connectTimeoutInMilliseconds"));
        var readTimeout = Duration.ofMillis(annotationAttributes.<Long>getNumber("readTimeoutInMilliseconds"));

        if (connectTimeout.isNegative() || connectTimeout.isZero()) {
            throw new IllegalArgumentException("connectTimeoutInMilliseconds must be positive, but got " + connectTimeout.toMillis());
        }
        if (readTimeout.isNegative() || readTimeout.isZero()) {
            throw new IllegalArgumentException("readTimeoutInMilliseconds must be positive, but got " + readTimeout.toMillis());
        }

        try {
            var factoryBean = new JdkClientHttpRequestFactoryBean();
            factoryBean.setTrustAllIfNoTrustStore(true);
            factoryBean.setConnectTimeout(connectTimeout);
            factoryBean.setReadTimeout(readTimeout);
            factoryBean.afterPropertiesSet();
            var clientFactory = factoryBean.getObject();

            var beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(ClientHttpRequestFactory.class, () -> clientFactory)
                            .getBeanDefinition();

            registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create unsafe ClientHttpRequestFactory", e);
        }
    }

}
