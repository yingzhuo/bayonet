package com.github.yingzhuo.bayonet.jwt.service;

import com.github.yingzhuo.bayonet.jwt.JwtDescriptor;
import org.jspecify.annotations.Nullable;

/**
 * JWT 验证结果。
 *
 * @param status     验证状态（非 {@code null}）
 * @param descriptor 验证通过时解码的 JWT 描述符，验证失败时为 {@code null}
 * @author 应卓
 * @since 4.1.1
 */
public record ValidatingResult(Status status, @Nullable JwtDescriptor descriptor) {

    /**
     * JWT 验证状态枚举。
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
    public enum Status {

        /**
         * 验证通过。
         */
        OK,

        /**
         * JWT 格式非法。
         */
        INVALID_JWT_FORMAT,

        /**
         * 签名无效。
         */
        INVALID_SIGNATURE,

        /**
         * 时间相关校验失败（过期、未生效等）。
         */
        INVALID_TIME,

        /**
         * 声明校验失败。
         */
        INVALID_CLAIM,

        /**
         * 已登出或被吊销。
         */
        INVALID_BLACKLISTED

    }
}
