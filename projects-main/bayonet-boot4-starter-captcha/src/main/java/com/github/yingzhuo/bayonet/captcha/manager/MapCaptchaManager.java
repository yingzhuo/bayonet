package com.github.yingzhuo.bayonet.captcha.manager;

import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 {@link ConcurrentHashMap} 的 {@link CaptchaManager} 实现。
 *
 * <p>验证码数据存储在 JVM 内存中，线程安全。适用于单机开发、测试及非生产环境。
 * 生产环境建议使用 {@link RedisCaptchaManager}。</p>
 *
 * @author 应卓
 * @see CaptchaManager
 * @see RedisCaptchaManager
 * @since 4.1.1
 */
public class MapCaptchaManager implements CaptchaManager {

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String saveKey, String captcha) {
        Assert.notNull(saveKey, "saveKey must not be null");
        Assert.notNull(captcha, "captcha must not be null");
        storage.put(saveKey, captcha);
    }

    @Override
    public @Nullable String load(String saveKey) {
        Assert.notNull(saveKey, "saveKey must not be null");
        return storage.get(saveKey);
    }

}
