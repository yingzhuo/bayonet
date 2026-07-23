package com.github.yingzhuo.bayonet.jwt.blacklist;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 无操作（Noop）的 {@link BlacklistManager} 实现。
 *
 * <p>所有令牌均视为非黑名单，即 {@link #isBlacklisted(String, String)} 始终返回 {@code false}。
 * 适用于不启用黑名单功能的场景。</p>
 *
 * @author 应卓
 * @see BlacklistManager
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopBlacklistManager implements BlacklistManager {

    /**
     * 获取 {@link NoopBlacklistManager} 单例实例。
     *
     * @return 单例实例
     */
    public static NoopBlacklistManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public boolean isBlacklisted(String rawToken, String jti) {
        return false;
    }

    @Override
    public void add(String rawToken, String jti) {
        // no-op
    }

    @Override
    public void remove(String rawToken, String jti) {
        // no-op
    }

    // -----------------------------------------------------------------------------------------------------------------

    private static class LazyHolder {
        private static final NoopBlacklistManager INSTANCE = new NoopBlacklistManager();
    }
}
