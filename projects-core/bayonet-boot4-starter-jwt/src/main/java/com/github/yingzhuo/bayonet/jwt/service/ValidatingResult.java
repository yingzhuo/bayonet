package com.github.yingzhuo.bayonet.jwt.service;

/**
 * JWT 验证结果枚举。
 * <p>表示 {@link JwtValidator#validate(String)} 的返回结果。</p>
 *
 * <ul>
 *   <li>{@link #OK} — 验证通过</li>
 *   <li>{@link #INVALID_JWT_FORMAT} — JWT 格式非法</li>
 *   <li>{@link #INVALID_SIGNATURE} — 签名无效</li>
 *   <li>{@link #INVALID_TIME} — 时间相关校验失败（过期等）</li>
 *   <li>{@link #INVALID_CLAIM} — 声明校验失败</li>
 *   <li>{@link #INVALID_BLACKLISTED} — 已登出或被吊销</li>
 * </ul>
 */
public enum ValidatingResult {

    /**
     * 验证通过
     */
    OK,

    /**
     * JWT 格式非法
     */
    INVALID_JWT_FORMAT,

    /**
     * 签名无效
     */
    INVALID_SIGNATURE,

    /**
     * 时间相关校验失败（过期、未生效等）
     */
    INVALID_TIME,

    /**
     * 声明校验失败
     */
    INVALID_CLAIM,

    /**
     * 已登出或被吊销
     */
    INVALID_BLACKLISTED

}
