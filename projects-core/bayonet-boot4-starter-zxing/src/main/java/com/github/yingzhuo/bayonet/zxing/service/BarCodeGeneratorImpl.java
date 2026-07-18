package com.github.yingzhuo.bayonet.zxing.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.util.Assert;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

public class BarCodeGeneratorImpl implements BarCodeGenerator {

    @Override
    public BufferedImage generate(String content, int width, int height) {
        Assert.hasText(content, "content must not be empty");
        Assert.isTrue(width > 0, "width must be positive");
        Assert.isTrue(height > 0, "height must be positive");

        try {
            var hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            hints.put(EncodeHintType.MARGIN, 1);

            var bitMatrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.CODE_128, width, height, hints);

            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

}
