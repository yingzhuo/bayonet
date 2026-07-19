package com.github.yingzhuo.bayonet.webmvc.support.ret;

import com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Objects;

/**
 * 图片返回值接口。
 * <p>控制器方法返回此类型时，由 {@link ImageRetHandlerMethodReturnValueHandler}
 * 直接写入 HTTP 响应，而非走视图解析。</p>
 *
 * <pre>{@code
 * @GetMapping("/image")
 * public ImageRet image() {
 *     return ImageRet.builder()
 *             .image(myImage)
 *             .contentType("image/png")
 *             .maxAge(3600)
 *             .build();
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public interface ImageRet extends Serializable {

    /**
     * 获取图片对象。
     *
     * @return BufferedImage，非 {@code null}
     */
    BufferedImage getImage();

    /**
     * 缓存时间（秒）。
     *
     * @return 缓存秒数，负数表示不设置缓存
     */
    default int maxAge() {
        return -1;
    }

    /**
     * 响应 Content-Type。
     *
     * @return Content-Type
     */
    default String contentType() {
        return "image/png";
    }

    /**
     * 文件名。
     *
     * @return 文件名
     */
    default String filename() {
        return "image.png";
    }

    /**
     * Content-Disposition 类型。
     *
     * @return ContentDispositionType
     */
    default ContentDispositionType contentDispositionType() {
        return ContentDispositionType.INLINE;
    }

    /**
     * 创建 {@link ImageRet} 构建器。
     *
     * @return Builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * {@link ImageRet} 构建器。
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Builder {

        private @Nullable BufferedImage image;
        private int maxAge = -1;
        private String contentType = "image/png";
        private String filename = "image.png";
        private ContentDispositionType contentDispositionType = ContentDispositionType.INLINE;

        /**
         * 设置图片（必填）。
         *
         * @param image BufferedImage
         * @return this
         * @throws NullPointerException image 为 {@code null} 时抛出
         */
        public Builder image(BufferedImage image) {
            this.image = Objects.requireNonNull(image, "image must not be null");
            return this;
        }

        /**
         * 设置缓存时间。
         *
         * @param maxAge 缓存秒数，负数表示不设置
         * @return this
         */
        public Builder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        /**
         * 设置 Content-Type。
         *
         * @param contentType Content-Type
         * @return this
         */
        public Builder contentType(String contentType) {
            this.contentType = Objects.requireNonNull(contentType);
            return this;
        }

        /**
         * 设置文件名。
         *
         * @param filename 文件名
         * @return this
         */
        public Builder filename(String filename) {
            this.filename = Objects.requireNonNull(filename);
            return this;
        }

        /**
         * 设置 Content-Disposition 类型。
         *
         * @param contentDispositionType ContentDispositionType
         * @return this
         */
        public Builder contentDispositionType(ContentDispositionType contentDispositionType) {
            this.contentDispositionType = Objects.requireNonNull(contentDispositionType);
            return this;
        }

        /**
         * 构建 {@link ImageRet} 实例。
         *
         * @return ImageRet
         * @throws NullPointerException image 未设置时抛出
         */
        public ImageRet build() {
            var img = Objects.requireNonNull(image, "image must not be null");
            var ct = this.contentType;
            var fn = this.filename;
            var cdt = this.contentDispositionType;
            var ma = this.maxAge;

            return new ImageRet() {
                @Override
                public BufferedImage getImage() {
                    return img;
                }

                @Override
                public int maxAge() {
                    return ma;
                }

                @Override
                public String contentType() {
                    return ct;
                }

                @Override
                public String filename() {
                    return fn;
                }

                @Override
                public ContentDispositionType contentDispositionType() {
                    return cdt;
                }
            };
        }
    }
}
