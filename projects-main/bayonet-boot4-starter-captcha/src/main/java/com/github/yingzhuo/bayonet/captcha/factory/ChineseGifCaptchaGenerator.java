package com.github.yingzhuo.bayonet.captcha.factory;

import com.wf.captcha.ChineseGifCaptcha;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 中文 GIF 动态验证码生成器。
 *
 * <p>使用 {@link ChineseGifCaptcha} 生成包含中文汉字的 GIF 格式动态验证码图片，支持自定义长度、字体和尺寸。</p>
 *
 * @author 应卓
 * @see ChineseGifCaptcha
 * @since 4.1.1
 */
@Getter
@Setter
public class ChineseGifCaptchaGenerator implements CaptchaGenerator<ChineseGifCaptcha> {

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
    public ChineseGifCaptcha generate() {
        var captcha = new ChineseGifCaptcha(width, height, len);
        if (font != null) {
            captcha.setFont(font);
        }
        return captcha;
    }

}
