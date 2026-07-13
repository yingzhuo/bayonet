package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.*;

/**
 * 无操作 JWT Algorithm 注册注解。
 * <p>标注在 {@link org.springframework.context.annotation.Configuration @Configuration} 类上，
 * 注册一个 {@link com.auth0.jwt.algorithms.Algorithm#none Algorithm.none()} Bean。
 * 该 Algorithm 不执行签名验证，适用于开发或测试环境。</p>
 *
 * <p><b>安全警告：</b>无签名验证的 Algorithm 在生产环境中使用将导致 JWT 令牌可被任意伪造。
 * 请仅在开发或内部测试场景中使用。</p>
 *
 * <pre>{@code
 * @Configuration
 * @NoOpAlgorithm
 * public class JwtConfig {
 * }
 * }</pre>
 *
 * @see com.auth0.jwt.algorithms.Algorithm#none
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(NoOpAlgorithm.Importing.class)
public @interface NoOpAlgorithm {

    /**
     * 无签名 JWT Algorithm 的 {@link ImportBeanDefinitionRegistrar} 实现。
     * <p>注册一个 {@link com.auth0.jwt.algorithms.Algorithm#none Algorithm.none()} 单例 Bean。</p>
     */
    class Importing implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
            var beanDef = BeanDefinitionBuilder.genericBeanDefinition(Algorithm.class, Algorithm::none)
                    .getBeanDefinition();
            var beanName = beanNameGenerator.generateBeanName(beanDef, registry);
            registry.registerBeanDefinition(beanName, beanDef);
        }
    }

}
