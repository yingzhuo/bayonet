package com.github.yingzhuo.bayonet.jwt.service;

import com.auth0.jwt.interfaces.Verification;

/**
 * JWT 验证配置定制器。
 * <p>用于在验证前对 {@link Verification} 进行额外配置，例如设置 leeway、required claims 等。</p>
 *
 * <pre>{@code
 * VerificationCustomizer customizer = v -> v.acceptLeeway(5);
 * var validator = new DefaultJwtValidator(algorithm, customizer);
 * }</pre>
 */
public interface VerificationCustomizer {

    /**
     * 定制 {@link Verification} 配置。
     *
     * @param verification Verification 实例
     */
    void customize(Verification verification);

}
