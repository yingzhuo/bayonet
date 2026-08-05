package com.github.yingzhuo.bayonet.jwt;

/**
 * JWT 头部（header）接口。
 * <p>提供 JWT 头部声明字段与头部声明的查询方法。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public interface Header {

    /**
     * 获取签名算法。
     *
     * @return 签名算法（如 {@code HS256}、{@code RS256} 等）
     */
    String getAlgorithm();

    /**
     * 获取头部类型。
     *
     * @return 头部类型，可能为 {@code null}
     */
    String getType();

    /**
     * 获取头部内容类型。
     *
     * @return 内容类型，可能为 {@code null}
     */
    String getContentType();

    /**
     * 获取密钥 ID。
     *
     * @return 密钥 ID，可能为 {@code null}
     */
    String getKeyId();

    /**
     * 按名称获取头部声明。
     *
     * @param name 声明名称
     * @return 声明（非 {@code null}）
     */
    Claim getHeaderClaim(String name);
}
