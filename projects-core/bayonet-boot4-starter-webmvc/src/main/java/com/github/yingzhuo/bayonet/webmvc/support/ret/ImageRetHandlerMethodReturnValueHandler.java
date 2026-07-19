package com.github.yingzhuo.bayonet.webmvc.support.ret;

import com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

/**
 * {@link ImageRet} 类型的返回值处理器。
 * <p>将控制器方法返回的 {@link ImageRet} 直接写入 HTTP 响应，
 * 支持缓存控制、文件名和内容处置类型配置。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class ImageRetHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ImageRet.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        mavContainer.setRequestHandled(true);

        if (returnValue == null) {
            return;
        }

        var imageRet = (ImageRet) returnValue;
        var image = imageRet.image();

        if (image == null) {
            return;
        }

        var response = ((ServletWebRequest) webRequest).getResponse();
        if (response == null) {
            return;
        }

        writeResponse(response, imageRet, image);
    }

    private void writeResponse(HttpServletResponse response, ImageRet imageRet, BufferedImage image) throws IOException {
        response.setStatus(imageRet.httpStatus().value());
        response.setContentType(imageRet.contentType());

        var maxAge = imageRet.maxAge();
        if (maxAge >= 0) {
            response.setHeader(HttpHeaders.CACHE_CONTROL,
                    CacheControl.maxAge(maxAge, TimeUnit.SECONDS).getHeaderValue());
        }

        var contentDispositionType = imageRet.contentDispositionType();
        var filename = imageRet.filename();

        if (contentDispositionType == ContentDispositionType.ATTACHMENT) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment().filename(filename).build().toString());
        } else {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.inline().filename(filename).build().toString());
        }

        var format = resolveFormat(imageRet.contentType());

        // 先写入字节数组获取长度，设置 Content-Length
        byte[] bytes;
        try (var baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            baos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        response.setContentLengthLong(bytes.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
    }

    private String resolveFormat(String contentType) {
        if (contentType == null) {
            return "png";
        }
        try {
            var mediaType = MediaType.parseMediaType(contentType);
            var subtype = mediaType.getSubtype();
            if ("jpeg".equals(subtype)) {
                return "jpg";
            }
            if ("svg+xml".equals(subtype)) {
                return "svg";
            }
            return subtype;
        } catch (Exception e) {
            return "png";
        }
    }

}
