package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Getter;
import lombok.Setter;

/**
 * 中文 PNG 验证码生成器。
 *
 * <p>使用 {@link ChineseCaptcha} 生成包含中文汉字的 PNG 格式验证码图片，支持自定义长度、字体和尺寸。</p>
 *
 * @author 应卓
 * @see ChineseCaptcha
 * @since 4.1.1
 */
@Getter
@Setter
public class ChineseCaptchaGenerator implements CaptchaGenerator<ChineseCaptcha> {

    /**
     * 图片宽度（默认 130）
     */
    private int width = 130;

    /**
     * 图片高度（默认 48）
     */
    private int height = 48;

    /**
     * 验证码字符长度（默认 4）
     */
    private int len = 4;

    /**
     * 字体索引（{@link Captcha#FONT_1} ~ {@link Captcha#FONT_10}），默认 {@code 0} 表示使用库默认字体
     */
    private int font = 0;

    @Override
    public ChineseCaptcha generate(Class<ChineseCaptcha> captchaKlass) {
        var captcha = new ChineseCaptcha(width, height, len);
        if (font > 0) {
            try {
                captcha.setFont(font);
            } catch (Exception ignored) {
            }
        }
        return captcha;
    }

}
