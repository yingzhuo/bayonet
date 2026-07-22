package com.github.yingzhuo.bayonet.webmvc.support.ret;

import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * {@link AttachmentRet} 类型的返回值处理器。
 * <p>将控制器方法返回的 {@link AttachmentRet} 直接写入 HTTP 响应，
 * 支持文件名、Content-Type 和 Content-Disposition 配置。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class AttachmentRetHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return AttachmentRet.class.isAssignableFrom(returnType.getParameterType());
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

        var attachmentRet = (AttachmentRet) returnValue;
        var inputStream = attachmentRet.inputStream();

        if (inputStream == null) {
            return;
        }

        var response = ((ServletWebRequest) webRequest).getResponse();
        if (response == null) {
            return;
        }

        writeResponse(response, attachmentRet, inputStream);
    }

    private void writeResponse(HttpServletResponse response, AttachmentRet attachmentRet, InputStream inputStream) throws IOException {
        response.setStatus(attachmentRet.httpStatus().value());
        response.setContentType(attachmentRet.contentType());

        var contentDispositionType = attachmentRet.contentDispositionType();
        var filename = attachmentRet.filename();

        if (contentDispositionType == com.github.yingzhuo.bayonet.webmvc.support.ContentDispositionType.ATTACHMENT) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment().filename(filename).build().toString());
        } else {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.inline().filename(filename).build().toString());
        }

        byte[] bytes;
        try (inputStream) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        response.setContentLengthLong(bytes.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
    }
}
