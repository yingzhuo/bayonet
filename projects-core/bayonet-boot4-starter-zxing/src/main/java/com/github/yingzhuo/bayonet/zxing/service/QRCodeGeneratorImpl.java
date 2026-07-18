package com.github.yingzhuo.bayonet.zxing.service;

import com.github.yingzhuo.bayonet.zxing.exception.WritingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

public class QRCodeGeneratorImpl implements QRCodeGenerator {

    @Override
    public BufferedImage generate(String content,
                                  @Nullable Logo logo,
                                  @Nullable ErrorCorrectionLevel errorCorrectionLevel,
                                  int size) {
        Assert.hasText(content, "content must not be empty");
        Assert.isTrue(size > 0, "size must be positive");

        try {
            var hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            hints.put(EncodeHintType.ERROR_CORRECTION,
                    errorCorrectionLevel != null ? errorCorrectionLevel : ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            var bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            var image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            if (logo != null) {
                insertLogo(image, logo, size);
            }

            return image;
        } catch (WriterException e) {
            throw new WritingException(e);
        }
    }

    // ------

    private void insertLogo(BufferedImage source, Logo logo, int qrCodeSize) {
        Image src = logo.getImage();
        var width = src.getWidth(null);
        var height = src.getHeight(null);

        if (logo.isCompress()) {
            width = Math.min(width, 60);
            height = Math.min(height, 60);
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            var tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }

        var graph = source.createGraphics();
        var x = (qrCodeSize - width) / 2;
        var y = (qrCodeSize - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        var shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

}
