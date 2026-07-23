package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Getter;
import lombok.Setter;

/**
 * 算术验证码生成器。
 *
 * <p>使用 {@link ArithmeticCaptcha} 生成包含算术表达式的 PNG 格式验证码图片，
 * 支持自定义尺寸、表达式长度和字体。</p>
 *
 * @author 应卓
 * @see ArithmeticCaptcha
 * @since 4.1.1
 */
@Getter
@Setter
public class ArithmeticCaptchaGenerator implements CaptchaGenerator<ArithmeticCaptcha> {

    /**
     * 图片宽度（默认 130）
     */
    private int width = 130;

    /**
     * 图片高度（默认 48）
     */
    private int height = 48;

    /**
     * 算术表达式长度（默认 2，即 {@code 1+1=?} 形式）
     */
    private int len = 2;

    /**
     * 字体索引（{@link Captcha#FONT_1} ~ {@link Captcha#FONT_10}），默认 {@code 0} 表示使用库默认字体
     */
    private int font = 0;

    @Override
    public ArithmeticCaptcha generate(Class<? extends Captcha> captchaKlass) {
        var captcha = new ArithmeticCaptcha(width, height, len);
        if (font > 0) {
            try {
                captcha.setFont(font);
            } catch (Exception ignored) {
            }
        }
        return captcha;
    }

}
