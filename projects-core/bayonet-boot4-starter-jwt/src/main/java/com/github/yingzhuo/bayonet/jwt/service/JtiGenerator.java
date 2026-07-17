package com.github.yingzhuo.bayonet.jwt.service;

/**
 * JWT ID (jti) 生成器接口。
 * <p>用于在 JWT 创建时为令牌生成唯一标识符 {@code jti} 声明。
 * 通常用于防止重放攻击。</p>
 *
 * <pre>{@code
 * JtiGenerator generator = () -> UUID.randomUUID().toString();
 * }</pre>
 *
 * @see DefaultJwtCreator
 */
public interface JtiGenerator {

    /**
     * 生成 JWT ID。
     *
     * @return JWT ID 字符串（非 {@code null}）
     */
    String generate();

}
