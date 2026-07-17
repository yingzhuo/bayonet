package com.github.yingzhuo.bayonet.jwt.service;

/**
 * JWT 验证器接口。
 * <p>验证 JWT token 的有效性并返回 {@link ValidatingResult}。</p>
 *
 * @see JwtCreator
 */
public interface JwtValidator {

    /**
     * 验证 JWT token。
     *
     * @param token JWT token 字符串
     * @return 验证结果
     */
    ValidatingResult validate(String token);

}
