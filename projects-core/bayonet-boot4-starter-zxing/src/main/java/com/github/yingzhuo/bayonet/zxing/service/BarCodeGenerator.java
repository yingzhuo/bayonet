package com.github.yingzhuo.bayonet.zxing.service;

import java.awt.image.BufferedImage;

/**
 * 条形码生成器接口。
 * <p>使用 ZXing 生成 CODE_128 格式条形码。</p>
 * @author 应卓
 */
public interface BarCodeGenerator {

    /**
     * 生成条形码图片。
     *
     * @param content 条形码内容，不能为空
     * @param width   图片宽度（像素）
     * @param height  图片高度（像素）
     * @return 条形码 {@link BufferedImage}
     */
    BufferedImage generate(String content, int width, int height);

}
