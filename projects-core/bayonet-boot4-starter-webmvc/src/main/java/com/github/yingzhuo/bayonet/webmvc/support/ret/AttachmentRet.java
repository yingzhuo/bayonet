package com.github.yingzhuo.bayonet.webmvc.support.ret;

import com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * 文件下载返回值接口。
 * <p>控制器方法返回此类型时，由 {@link AttachmentRetHandlerMethodReturnValueHandler}
 * 直接写入 HTTP 响应，支持文件名、Content-Disposition 和状态码配置。</p>
 *
 * <pre>{@code
 * &#64;GetMapping("/download")
 * public AttachmentRet download() {
 *     return AttachmentRet.builder()
 *             .inputStream(inputStream)
 *             .filename("report.pdf")
 *             .contentType("application/pdf")
 *             .build();
 * }
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public interface AttachmentRet extends Serializable {

    /**
     * 创建 {@link AttachmentRet} 构建器。
     *
     * @return Builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * 获取文件输入流。
     * <p>该 InputStream 由 {@link AttachmentRetHandlerMethodReturnValueHandler}
     * 在消费完毕后自动关闭，调用方无需手动关闭。</p>
     *
     * @return InputStream，非 {@code null}
     */
    InputStream inputStream();

    /**
     * 文件名。
     *
     * @return 文件名
     */
    String filename();

    /**
     * 响应 Content-Type。
     *
     * @return Content-Type
     */
    default String contentType() {
        return "application/octet-stream";
    }

    /**
     * Content-Disposition 类型。
     *
     * @return ContentDispositionType
     */
    default ContentDispositionType contentDispositionType() {
        return ContentDispositionType.ATTACHMENT;
    }

    /**
     * 响应 HTTP 状态码。
     *
     * @return HTTP 状态码，默认 {@link HttpStatus#OK}
     */
    default HttpStatus httpStatus() {
        return HttpStatus.OK;
    }

    /**
     * {@link AttachmentRet} 构建器。
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Builder {

        private @Nullable InputStream inputStream;
        private @Nullable String filename;
        private String contentType = "application/octet-stream";
        private ContentDispositionType contentDispositionType = ContentDispositionType.ATTACHMENT;
        private HttpStatus httpStatus = HttpStatus.OK;

        /**
         * 设置文件输入流（必填）。
         * <p>该 InputStream 由 Handler 在消费完毕后自动关闭，调用方无需手动关闭。</p>
         *
         * @param inputStream InputStream
         * @return this
         * @throws NullPointerException inputStream 为 {@code null} 时抛出
         */
        public Builder inputStream(InputStream inputStream) {
            this.inputStream = Objects.requireNonNull(inputStream, "inputStream must not be null");
            return this;
        }

        /**
         * 设置文件名（必填）。
         *
         * @param filename 文件名
         * @return this
         * @throws NullPointerException filename 为 {@code null} 时抛出
         */
        public Builder filename(String filename) {
            this.filename = Objects.requireNonNull(filename, "filename must not be null");
            return this;
        }

        /**
         * 设置 Content-Type。
         *
         * @param contentType Content-Type
         * @return this
         * @throws NullPointerException contentType 为 {@code null} 时抛出
         */
        public Builder contentType(String contentType) {
            this.contentType = Objects.requireNonNull(contentType);
            return this;
        }

        /**
         * 设置 Content-Disposition 类型。
         *
         * @param contentDispositionType ContentDispositionType
         * @return this
         * @throws NullPointerException contentDispositionType 为 {@code null} 时抛出
         */
        public Builder contentDispositionType(ContentDispositionType contentDispositionType) {
            this.contentDispositionType = Objects.requireNonNull(contentDispositionType);
            return this;
        }

        /**
         * 设置 HTTP 状态码。
         *
         * @param httpStatus HTTP 状态码
         * @return this
         * @throws NullPointerException httpStatus 为 {@code null} 时抛出
         */
        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = Objects.requireNonNull(httpStatus);
            return this;
        }

        /**
         * 构建 {@link AttachmentRet} 实例。
         *
         * @return AttachmentRet
         * @throws NullPointerException inputStream 或 filename 未设置时抛出
         */
        public AttachmentRet build() {
            var is = Objects.requireNonNull(inputStream, "inputStream must not be null");
            var fn = Objects.requireNonNull(filename, "filename must not be null");
            var ct = this.contentType;
            var cdt = this.contentDispositionType;

            return new AttachmentRet() {
                @Override
                public InputStream inputStream() {
                    return is;
                }

                @Override
                public String filename() {
                    return fn;
                }

                @Override
                public String contentType() {
                    return ct;
                }

                @Override
                public ContentDispositionType contentDispositionType() {
                    return cdt;
                }

                @Override
                public HttpStatus httpStatus() {
                    return httpStatus;
                }
            };
        }
    }
}
