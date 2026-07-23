package com.github.yingzhuo.bayonet.captcha.manager;

import org.jspecify.annotations.Nullable;

/**
 * 验证码管理器接口。
 *
 * <p>提供验证码的保存和校验能力，支持不同的存储后端。
 * 内置实现：</p>
 * <ul>
 *   <li>{@link MapCaptchaManager} — 基于内存 {@link java.util.concurrent.ConcurrentHashMap}，适用于开发测试</li>
 *   <li>{@link RedisCaptchaManager} — 基于 Redis，适用于生产环境</li>
 * </ul>
 *
 * @author 应卓
 * @see MapCaptchaManager
 * @see RedisCaptchaManager
 * @since 4.1.1
 */
public interface CaptchaManager {

    /**
     * 保存验证码。
     *
     * @param saveKey 存储 key，不能为 {@code null}
     * @param captcha 验证码文本，不能为 {@code null}
     */
    void save(String saveKey, String captcha);

    /**
     * 读取验证码。
     *
     * @param saveKey 存储 key，不能为 {@code null}
     * @return 验证码文本，不存在或已过期时返回 {@code null}
     */
    @Nullable
    String load(String saveKey);

    /**
     * 删除验证码。
     *
     * @param saveKey 存储 key，不能为 {@code null}
     * @return {@code true} 如果 key 存在且删除成功，否则 {@code false}
     */
    boolean delete(String saveKey);
}
