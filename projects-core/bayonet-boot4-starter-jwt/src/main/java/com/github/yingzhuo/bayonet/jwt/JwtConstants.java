package com.github.yingzhuo.bayonet.jwt;

/**
 * JWT 标准声明名称常量。
 * <p>定义 JWT header 和 payload 中使用的标准字段名称，
 * 对应 <a href="https://www.rfc-editor.org/rfc/rfc7519">RFC 7519</a> 规范。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
public interface JwtConstants {

    /**
     * Header: 令牌类型
     */
    String HEADER_TYPE = "typ";
    /**
     * Header: 密钥标识
     */
    String HEADER_KEY_ID = "kid";
    /**
     * Header: 内容类型
     */
    String HEADER_CONTENT_TYPE = "cty";

    // ------

    /**
     * Payload: 签发者
     */
    String PAYLOAD_ISSUER = "iss";
    /**
     * Payload: 主题
     */
    String PAYLOAD_SUBJECT = "sub";
    /**
     * Payload: 受众
     */
    String PAYLOAD_AUDIENCE = "aud";
    /**
     * Payload: 过期时间
     */
    String PAYLOAD_EXPIRES = "exp";
    /**
     * Payload: 生效时间
     */
    String PAYLOAD_NOT_BEFORE = "nbf";
    /**
     * Payload: 签发时间
     */
    String PAYLOAD_ISSUED_AT = "iat";
    /**
     * Payload: JWT ID
     */
    String PAYLOAD_JWT_ID = "jti";

}
