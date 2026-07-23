package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 标准 PNG 验证码生成器（字母/数字）。
 *
 * <p>使用 {@link SpecCaptcha} 生成 PNG 格式的验证码图片，支持自定义字符类型、长度、字体和尺寸。</p>
 *
 * @author 应卓
 * @see SpecCaptcha
 * @since 4.1.1
 */
@Getter
@Setter
public class SpecCaptchaGenerator implements CaptchaGenerator<SpecCaptcha> {

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
     * 字体
     */
    private Font font;

    @Override
    public SpecCaptcha generate() {
        var captcha = new SpecCaptcha(width, height, len);
        captcha.setCharType(charType);
        if (font != null) {
            captcha.setFont(font);
        }
        return captcha;
    }

}
