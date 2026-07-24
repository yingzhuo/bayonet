package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.beandef.BeanDefinitionRegistrarSupport;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;

/**
 * HMAC 签名 JWT Algorithm 注册注解。
 * <p>标注在 {@link org.springframework.context.annotation.Configuration @Configuration} 类上，
 * 注册一个基于 HMAC（对称密钥）的 {@link com.auth0.jwt.algorithms.Algorithm Algorithm} Bean。
 * 支持 HmacSHA256、HmacSHA384、HmacSHA512 三种算法。</p>
 *
 * <pre>{@code
 * @Configuration
 * @HMACAlgorithm(
 *     secret        = "${app.jwt.secret}",
 *     algorithmName = HMACAlgorithm.Type.HMAC256
 * )
 * public class JwtConfig {
 * }
 * }</pre>
 *
 * @author 应卓
 * @see com.auth0.jwt.algorithms.Algorithm
 * @see Type
 * @since 4.1.0
 * @deprecated 这种配置方式不易理解，稍有炫技成分。
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HMACAlgorithm.Importing.class)
@ApiStatus.Experimental
@Deprecated(since = "4.1.1", forRemoval = true)
public @interface HMACAlgorithm {

    /**
     * HMAC 密钥值（支持 {@code ${...}} 占位符解析）。
     *
     * @return 密钥字符串
     */
    String secret();

    /**
     * HMAC 算法类型。
     *
     * @return 算法类型
     */
    Type algorithmName() default Type.HMAC384;

    // ------

    /**
     * HMAC 算法类型枚举。
     * <p>支持的 HMAC 签名算法：</p>
     * <ul>
     *     <li>{@link #HMAC256} — HmacSHA256</li>
     *     <li>{@link #HMAC384} — HmacSHA384</li>
     *     <li>{@link #HMAC512} — HmacSHA512</li>
     * </ul>
     */
    @ApiStatus.Experimental
    @Deprecated(since = "4.1.1")
    enum Type {
        HMAC256, HMAC384, HMAC512
    }

    /**
     * HMAC JWT Algorithm 的 {@link ImportBeanDefinitionRegistrar} 实现。
     * <p>读取 {@link HMACAlgorithm} 注解属性，创建对应的 HMAC Algorithm Bean。</p>
     */
    @ApiStatus.Experimental
    @Deprecated(since = "4.1.1")
    class Importing extends BeanDefinitionRegistrarSupport {

        public Importing(ResourceLoader resourceLoader, Environment environment, BeanFactory beanFactory, ClassLoader beanClassLoader) {
            super(resourceLoader, environment, beanFactory, beanClassLoader);
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
            var attributes = super.getAnnotationAttributes(importingClassMetadata, HMACAlgorithm.class);

            var secret = environment.resolvePlaceholders(attributes.getString("secret"));
            var algorithmName = attributes.<Type>getEnum("algorithmName");

            var alg = switch (algorithmName) {
                case HMAC256 -> Algorithm.HMAC256(secret);
                case HMAC384 -> Algorithm.HMAC384(secret);
                case HMAC512 -> Algorithm.HMAC512(secret);
            };

            var beanDef = BeanDefinitionBuilder.genericBeanDefinition(Algorithm.class, () -> alg)
                    .getBeanDefinition();

            var beanName = beanNameGenerator.generateBeanName(beanDef, registry);
            registry.registerBeanDefinition(beanName, beanDef);
        }
    }
}
