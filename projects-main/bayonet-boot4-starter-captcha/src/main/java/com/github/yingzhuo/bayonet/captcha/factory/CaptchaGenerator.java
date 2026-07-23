package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.base.Captcha;

/**
 * 验证码生成器接口。
 *
 * <p>每调用一次 {@link #generate()} 生成一个包含随机内容的验证码实例，
 * 通过 {@link Captcha} 提供的方法可获取验证码图片字节数据和文本内容。</p>
 *
 * @param <T> 验证码类型
 * @author 应卓
 * @see Captcha
 * @see SpecCaptchaGenerator
 * @see GifCaptchaGenerator
 * @see ChineseCaptchaGenerator
 * @see ChineseGifCaptchaGenerator
 * @see ArithmeticCaptchaGenerator
 * @since 4.1.1
 */
public interface CaptchaGenerator<T extends Captcha> {

    /**
     * 生成验证码。
     *
     * @return 验证码实例
     */
    <T extends Captcha> T generate();

}
