package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.awt.*;

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
     * 字体，为 {@code null} 时使用库默认字体
     */
    @Nullable
    private Font font;

    @Override
    public ArithmeticCaptcha generate() {
        var captcha = new ArithmeticCaptcha(width, height, len);
        if (font != null) {
            captcha.setFont(font);
        }
        return captcha;
    }

}
