package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 基于 KeyStore 的 JWT Algorithm 注册注解。
 * <p>标注在 {@link org.springframework.context.annotation.Configuration @Configuration} 类上，
 * 通过 {@link KeyStoreAlgorithmImporting} 自动注册一个 {@link com.auth0.jwt.algorithms.Algorithm Algorithm} Bean。</p>
 *
 * <pre>{@code
 * @Configuration
 * @KeyStoreAlgorithm(
 *     location = "classpath:keys.p12",
 *     storepass     = "changeit",
 *     alias         = "mykey",
 *     algorithmName = AlgorithmName.RSA256
 * )
 * public class JwtConfig {
 * }
 * }</pre>
 *
 * @see AlgorithmName
 * @see KeyStoreType
 * @author 应卓
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(KeyStoreAlgorithmImporting.class)
public @interface KeyStoreAlgorithm {

    /**
     * 密钥库类型。
     *
     * @return 密钥库类型
     */
    KeyStoreType type() default KeyStoreType.PKCS12;

    /**
     * 密钥库资源路径（支持 {@code classpath:}、{@code file:} 等 Spring 资源协议）。
     *
     * @return 资源路径
     */
    String location();

    /**
     * 密钥库密码。
     *
     * @return 密钥库密码
     */
    String storepass();

    /**
     * 密钥别名。
     *
     * @return 密钥别名
     */
    String alias();

    /**
     * 密钥密码（可选）。未指定时使用 {@link #storepass()}。
     *
     * @return 密钥密码
     */
    String keypass() default "";

    /**
     * 算法名称。
     *
     * @return 算法名称
     */
    AlgorithmName algorithmName();

    /**
     * 是否设为 {@code @Primary} Bean。
     *
     * @return 是否为主 Bean
     */
    boolean primary() default false;

    /**
     * 是否设置 Bean 的别名
     *
     * @return Bean 的别名
     */
    String[] beanAliases() default {};

}
