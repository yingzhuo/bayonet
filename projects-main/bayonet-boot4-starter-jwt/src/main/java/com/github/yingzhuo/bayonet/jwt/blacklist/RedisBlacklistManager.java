package com.github.yingzhuo.bayonet.jwt.blacklist;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 基于 Redis 的 {@link BlacklistManager} 实现。
 *
 * <p>使用 {@link StringRedisTemplate} 操作 Redis，通过 JWT 的 {@code jti} 声明判断令牌是否已被拉黑。
 * 支持自定义 key 前缀和过期时间，适用于分布式生产环境。</p>
 *
 * @author 应卓
 * @see BlacklistManager
 * @see StringRedisTemplate
 * @since 4.1.1
 */
public class RedisBlacklistManager implements BlacklistManager {

    private final StringRedisTemplate redisTemplate;

    /**
     * Redis key 前缀，默认 {@code jwt:blacklist:}
     */
    @Setter
    private String keyPrefix = "jwt:blacklist:";

    /**
     * 黑名单条目过期时间，默认 1 小时
     */
    @Setter
    private Duration ttl = Duration.ofHours(1);

    public RedisBlacklistManager(StringRedisTemplate redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate must not be null");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isBlacklisted(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        return Boolean.TRUE.equals(redisTemplate.hasKey(resolveKey(rawToken, jti)));
    }

    @Override
    public void add(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        redisTemplate.opsForValue().set(resolveKey(rawToken, jti), "1", ttl);
    }

    @Override
    public void remove(String rawToken, @Nullable String jti) {
        Assert.notNull(rawToken, "rawToken must not be null");
        redisTemplate.delete(resolveKey(rawToken, jti));
    }

    private String resolveKey(String rawToken, @Nullable String jti) {
        return keyPrefix + (jti != null ? jti : rawToken);
    }
}
