package com.github.yingzhuo.bayonet.jwt.blacklist;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 基于 Caffeine 本地缓存的 {@link BlacklistManager} 实现。
 *
 * <p>黑名单数据存储在 JVM 内存中，支持自动过期。无需外部依赖，适合单机生产环境。
 * 分布式环境建议使用 {@link RedisBlacklistManager}。</p>
 *
 * @author 应卓
 * @see BlacklistManager
 * @see RedisBlacklistManager
 * @since 4.1.1
 */
public class CaffeineBlacklistManager implements BlacklistManager {

    /**
     * 黑名单条目过期时间，默认 1 小时
     */
    @Setter
    private Duration ttl = Duration.ofHours(1);

    private volatile Cache<String, Boolean> cache;

    private static String resolveKey(String rawToken, @Nullable String jti) {
        return jti != null ? jti : rawToken;
    }

    @Override
    public boolean isBlacklisted(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        return Boolean.TRUE.equals(getOrCreate().getIfPresent(resolveKey(rawToken, jti)));
    }

    @Override
    public void add(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        getOrCreate().put(resolveKey(rawToken, jti), Boolean.TRUE);
    }

    @Override
    public void remove(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        getOrCreate().invalidate(resolveKey(rawToken, jti));
    }

    private Cache<String, Boolean> getOrCreate() {
        var result = this.cache;
        if (result == null) {
            synchronized (this) {
                result = this.cache;
                if (result == null) {
                    result = Caffeine.newBuilder()
                            .expireAfterWrite(ttl)
                            .build();
                    this.cache = result;
                }
            }
        }
        return result;
    }
}
