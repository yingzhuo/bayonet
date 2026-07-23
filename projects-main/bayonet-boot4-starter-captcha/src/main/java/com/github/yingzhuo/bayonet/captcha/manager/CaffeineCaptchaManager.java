package com.github.yingzhuo.bayonet.captcha.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 基于 Caffeine 本地缓存的 {@link CaptchaManager} 实现。
 *
 * <p>验证码数据存储在 JVM 内存中，支持自动过期和最大容量限制。
 * 无需外部依赖，适合单机生产环境。
 * 分布式环境建议使用 {@link RedisCaptchaManager}。</p>
 *
 * @author 应卓
 * @see CaptchaManager
 * @see RedisCaptchaManager
 * @since 4.1.1
 */
public class CaffeineCaptchaManager implements CaptchaManager {

    /**
     * 验证码过期时间，默认 5 分钟
     */
    @Setter
    private Duration ttl = Duration.ofMinutes(5);

    /**
     * 最大缓存条目数，默认 10_000
     */
    @Setter
    private long maximumSize = 10_000L;

    private volatile Cache<String, String> cache;

    @Override
    public void save(String saveKey, String captcha) {
        Assert.notNull(saveKey, "saveKey must not be null");
        Assert.notNull(captcha, "captcha must not be null");
        getOrCreate().put(saveKey, captcha);
    }

    @Override
    public @Nullable String load(String saveKey) {
        Assert.notNull(saveKey, "saveKey must not be null");
        return getOrCreate().getIfPresent(saveKey);
    }

    private Cache<String, String> getOrCreate() {
        var result = this.cache;
        if (result == null) {
            synchronized (this) {
                result = this.cache;
                if (result == null) {
                    result = Caffeine.newBuilder()
                            .expireAfterWrite(ttl)
                            .maximumSize(maximumSize)
                            .build();
                    this.cache = result;
                }
            }
        }
        return result;
    }
}
