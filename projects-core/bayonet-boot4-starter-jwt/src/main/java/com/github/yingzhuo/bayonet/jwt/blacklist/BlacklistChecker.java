package com.github.yingzhuo.bayonet.jwt.blacklist;

import org.jspecify.annotations.Nullable;

/**
 * JWT 黑名单检查策略。
 * <p>用于在 JWT 认证流程中判断令牌是否已被拉黑（如用户登出、令牌吊销等场景）。
 * 实现类需保证线程安全。</p>
 *
 * @see com.github.yingzhuo.bayonet.jwt.validator.DefaultJwtValidator
 */
public interface BlacklistChecker {

    /**
     * 检查指定令牌是否已被加入黑名单。
     *
     * @param rawToken 原始 JWT 令牌字符串（非 {@code null}）
     * @param jti      JWT ID（JWT payload 中的 {@code jti} 声明），可能为 {@code null}
     * @return 已在黑名单中返回 {@code true}，否则 {@code false}
     */
    boolean isBlacklisted(String rawToken, @Nullable String jti);

}
