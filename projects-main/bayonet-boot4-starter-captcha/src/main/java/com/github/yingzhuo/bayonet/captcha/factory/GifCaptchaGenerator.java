package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Getter;
import lombok.Setter;

/**
 * GIF 动态验证码生成器（字母/数字）。
 *
 * <p>使用 {@link GifCaptcha} 生成 GIF 格式的动态验证码图片，支持自定义字符类型、长度、字体和尺寸。</p>
 *
 * @author 应卓
 * @see GifCaptcha
 * @since 4.1.1
 */
@Getter
@Setter
public class GifCaptchaGenerator implements CaptchaGenerator<GifCaptcha> {

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
     * 字符类型（默认 {@link Captcha#TYPE_DEFAULT}）
     */
    private int charType = Captcha.TYPE_DEFAULT;

    /**
     * 字体索引（{@link Captcha#FONT_1} ~ {@link Captcha#FONT_10}），默认 {@code 0} 表示使用库默认字体
     */
    private int font = 0;

    @Override
    public GifCaptcha generate(Class<GifCaptcha> captchaKlass) {
        var captcha = new GifCaptcha(width, height, len);
        captcha.setCharType(charType);
        if (font > 0) {
            try {
                captcha.setFont(font);
            } catch (Exception ignored) {
            }
        }
        return captcha;
    }

}
