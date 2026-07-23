package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.ChineseCaptcha;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

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
     * 字体
     */
    private Font font;

    @Override
    public ChineseCaptcha generate() {
        var captcha = new ChineseCaptcha(width, height, len);
        if (font != null) {
            captcha.setFont(font);
        }
        return captcha;
    }

}
