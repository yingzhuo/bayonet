package com.github.yingzhuo.bayonet.zxing.service;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.jspecify.annotations.Nullable;

import java.awt.image.BufferedImage;

/**
 * 二维码生成器接口。
 * <p>使用 ZXing 生成二维码，支持自定义 Logo 和纠错级别。</p>
 */
public interface QRCodeGenerator {

    /**
     * 生成二维码图片。
     *
     * @param content              二维码内容，不能为空
     * @param logo                 Logo 图片，可为 {@code null}
     * @param errorCorrectionLevel 纠错级别，为 {@code null} 时默认 {@link ErrorCorrectionLevel#H}
     * @param size                 二维码图片尺寸（像素，宽高相等）
     * @return 二维码 {@link BufferedImage}
     */
    BufferedImage generate(String content,
                           @Nullable Logo logo,
                           @Nullable ErrorCorrectionLevel errorCorrectionLevel,
                           int size);

}
