package com.github.yingzhuo.bayonet.captcha.manager;

/**
 * 验证码存储 key 生成器接口。
 *
 * <p>每次调用 {@link #generate()} 生成一个唯一的存储 key，
 * 用于 {@link com.github.yingzhuo.bayonet.captcha.manager.CaptchaManager#save(String, String) CaptchaManager.save()}。</p>
 *
 * @author 应卓
 * @see UUIDSaveKeyGenerator
 * @see com.github.yingzhuo.bayonet.captcha.manager.CaptchaManager
 * @since 4.1.1
 */
@FunctionalInterface
public interface SaveKeyGenerator {

    /**
     * 生成存储 key。
     *
     * @return 唯一的存储 key
     */
    String generate();

}
