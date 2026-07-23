package com.github.yingzhuo.bayonet.captcha.manager;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 基于 Redis 的 {@link CaptchaManager} 实现。
 *
 * <p>验证码数据存储在 Redis 中，通过 {@link StringRedisTemplate} 操作。
 * 支持自动过期（利用 Redis TTL），适用于分布式生产环境。</p>
 *
 * <p>使用时需确保 {@link StringRedisTemplate} Bean 已存在。</p>
 *
 * @author 应卓
 * @see CaptchaManager
 * @see StringRedisTemplate
 * @since 4.1.1
 */
public class RedisCaptchaManager implements CaptchaManager {

    private final StringRedisTemplate redisTemplate;

    /**
     * 验证码过期时间，默认 5 分钟
     */
    @Setter
    private Duration ttl = Duration.ofMinutes(5);

    /**
     * Redis key 前缀，默认 {@code captcha:}
     */
    @Setter
    private String keyPrefix = "captcha:";

    public RedisCaptchaManager(StringRedisTemplate redisTemplate) {
        Assert.notNull(redisTemplate, "redisTemplate must not be null");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String saveKey, String captcha) {
        Assert.notNull(saveKey, "saveKey must not be null");
        Assert.notNull(captcha, "captcha must not be null");
        redisTemplate.opsForValue().set(keyPrefix + saveKey, captcha, ttl);
    }

    @Override
    public @Nullable String load(String saveKey) {
        Assert.notNull(saveKey, "saveKey must not be null");
        return redisTemplate.opsForValue().get(keyPrefix + saveKey);
    }

    @Override
    public boolean delete(String saveKey) {
        Assert.notNull(saveKey, "saveKey must not be null");
        return Boolean.TRUE.equals(redisTemplate.delete(keyPrefix + saveKey));
    }
}
