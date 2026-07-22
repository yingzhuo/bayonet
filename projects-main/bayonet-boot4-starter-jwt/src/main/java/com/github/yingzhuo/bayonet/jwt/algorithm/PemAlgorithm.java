package com.github.yingzhuo.bayonet.jwt.algorithm;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 基于 PEM 文件的 JWT Algorithm 注册注解。
 * <p>标注在 {@link org.springframework.context.annotation.Configuration @Configuration} 类上，
 * 通过 {@link PemAlgorithmImporting} 自动注册一个 {@link com.auth0.jwt.algorithms.Algorithm Algorithm} Bean。
 * 支持 PEM 编码的 X.509 证书和 PKCS#8 私钥。</p>
 *
 * <pre>{@code
 * @Configuration
 * @PemAlgorithm(
 *     location      = "classpath:keys/key.pem",
 *     keypass       = "changeit",
 *     algorithmName = AlgorithmName.ECDSA256
 * )
 * public class JwtConfig {
 * }
 * }</pre>
 *
 * @author 应卓
 * @see AlgorithmName
 * @since 4.1.0
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PemAlgorithmImporting.class)
public @interface PemAlgorithm {

    /**
     * PEM 文件资源路径（支持 {@code classpath:}、{@code file:} 等 Spring 资源协议）。
     *
     * @return 资源路径
     */
    String location();

    /**
     * 私钥密码（可选）。PEM 文件中的私钥若加密则需要提供。
     *
     * @return 私钥密码
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
