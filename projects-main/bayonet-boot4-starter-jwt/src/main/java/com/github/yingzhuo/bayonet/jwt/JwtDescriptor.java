package com.github.yingzhuo.bayonet.jwt;

import java.io.Serializable;

/**
 * JWT 描述符接口。
 * <p>同时提供 JWT 的头部（{@link Header}）与负载（{@link Payload}）信息，
 * 以及 JWT 原始字符串和 base64 编码的三个组成部分。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface JwtDescriptor extends Header, Payload, Serializable {

    /**
     * 获取 JWT 原始字符串。
     *
     * @return JWT 原始字符串（非 {@code null}）
     */
    String getJwtItself();

    /**
     * 获取 base64 编码的头部部分。
     *
     * @return base64 编码的头部（非 {@code null}）
     */
    String getHeaderPart();

    /**
     * 获取 base64 编码的负载部分。
     *
     * @return base64 编码的负载（非 {@code null}）
     */
    String getPayloadPart();

    /**
     * 获取 base64 编码的签名部分。
     *
     * @return base64 编码的签名（非 {@code null}）
     */
    String getSignaturePart();
}
