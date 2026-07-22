package com.github.yingzhuo.bayonet.jwt.service;

/**
 * JWT 创建器接口。
 * <p>将 {@link JwtData} 中的数据签名生成 JWT token。</p>
 *
 * @author 应卓
 * @see JwtValidator
 * @since 4.1.0
 */
public interface JwtCreator {

    /**
     * 创建 JWT token。
     *
     * @param data JWT 数据（header 和 payload）
     * @return JWT token 字符串
     */
    String create(JwtData data);

}
