package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * 图片工具类。
 *
 * <p>提供 {@link BufferedImage} 的字节数组转换和 Base64 编码功能。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * byte[] bytes = ImageUtils.toByteArray(image, "png");
 * String base64 = ImageUtils.encodeToBase64(image, "jpg");
 * }</pre>
 *
 * @author 应卓
 * @see BufferedImage
 * @see ImageIO
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageUtils {

    /**
     * 将图片转换为字节数组。
     *
     * @param image  图片对象，不能为 {@code null}
     * @param format 图片格式（如 {@code "png"}、{@code "jpg"}），不能为 {@code null}
     * @return 字节数组
     */
    public static byte[] toByteArray(BufferedImage image, String format) {
        try {
            var os = new ByteArrayOutputStream();
            ImageIO.write(image, format, os);
            return os.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 将图片编码为 Base64 字符串。
     *
     * @param image  图片对象，不能为 {@code null}
     * @param format 图片格式（如 {@code "png"}、{@code "jpg"}），不能为 {@code null}
     * @return Base64 编码字符串
     */
    public static String encodeToBase64(BufferedImage image, String format) {
        var bytes = toByteArray(image, format);
        var base64Bytes = Base64Utils.encode(bytes, false, true);
        return new String(base64Bytes, StandardCharsets.UTF_8);
    }

}
