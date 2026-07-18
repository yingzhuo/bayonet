package com.github.yingzhuo.bayonet.zxing.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.*;
import java.util.Objects;

/**
 * 二维码 Logo 图片封装。
 * <p>使用 Builder 模式从多种来源构建 Logo：
 * {@link Image}、{@link Resource}、{@link InputStream}、{@link ImageInputStream}、{@link File}。</p>
 *
 * <pre>{@code
 * var logo = Logo.builder()
 *         .image(new ClassPathResource("logo.png"))
 *         .compress(true)
 *         .build();
 * }</pre>
 *
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Logo implements Serializable {

    @Nullable
    private Image image;

    @Getter
    private boolean compress = true;

    /**
     * 获取 Builder 实例。
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取 Logo 图片。
     *
     * @return Image（非 {@code null}）
     * @throws NullPointerException image 尚未设置时抛出
     */
    public Image getImage() {
        return Objects.requireNonNull(image);
    }

    // ------

    /**
     * Logo Builder。
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private @Nullable Image image;
        private boolean compress = true;

        /**
         * 设置 Logo 图片。
         *
         * @param image Image
         * @return this
         */
        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        /**
         * 从 {@link Resource} 加载 Logo 图片。
         * <p>支持 classpath 和文件系统资源。</p>
         *
         * @param resource Resource
         * @return this
         */
        public Builder image(Resource resource) {
            try (var is = resource.getInputStream()) {
                this.image = ImageIO.read(is);
                return this;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        /**
         * 从 {@link InputStream} 加载 Logo 图片。
         * <p>注意：调用方需自行管理流的生命周期。</p>
         *
         * @param inputStream InputStream
         * @return this
         */
        public Builder image(InputStream inputStream) {
            try {
                this.image = ImageIO.read(inputStream);
                return this;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        /**
         * 从 {@link ImageInputStream} 加载 Logo 图片。
         * <p>注意：调用方需自行管理流的生命周期。</p>
         *
         * @param inputStream ImageInputStream
         * @return this
         */
        public Builder image(ImageInputStream inputStream) {
            try {
                this.image = ImageIO.read(inputStream);
                return this;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        /**
         * 从 {@link File} 加载 Logo 图片。
         *
         * @param file 图片文件
         * @return this
         */
        public Builder image(File file) {
            try {
                this.image = ImageIO.read(file);
                return this;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        /**
         * 设置是否压缩 Logo（缩小到最大 60px）。
         *
         * @param compress 是否压缩
         * @return this
         */
        public Builder compress(boolean compress) {
            this.compress = compress;
            return this;
        }

        /**
         * 构建 Logo 实例。
         *
         * @return Logo
         * @throws IllegalArgumentException image 未设置时抛出
         */
        public Logo build() {
            Assert.notNull(this.image, "image is not set");
            Logo logo = new Logo();
            logo.image = Objects.requireNonNull(image);
            logo.compress = this.compress;
            return logo;
        }
    }
}
