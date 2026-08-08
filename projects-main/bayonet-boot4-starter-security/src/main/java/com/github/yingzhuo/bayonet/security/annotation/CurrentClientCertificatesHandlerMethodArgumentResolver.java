package com.github.yingzhuo.bayonet.security.annotation;

import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * 解析 {@link CurrentClientCertificates @CurrentClientCertificates} 注解参数的
 * {@link HandlerMethodArgumentResolver}。
 *
 * <p>从 request attribute {@code jakarta.servlet.request.X509Certificate} 读取
 * Servlet 容器填充的客户端证书链，注入为 {@code List<X509Certificate>} 类型的参数。
 * 仅支持 {@code List} 类型参数（如 {@code List<X509Certificate>}）。</p>
 *
 * <p><b>注意：</b>客户端证书仅在服务器启用双向 TLS
 * （{@code server.ssl.client-auth=need} 或 {@code want}）时才会由容器填充；
 * 未启用或证书缺失时注入空列表。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @GetMapping("/me")
 * public String me(@CurrentClientCertificates List<X509Certificate> certificates) {
 *     return String.valueOf(certificates.size());
 * }
 * }</pre>
 *
 * @author 应卓
 * @see CurrentClientCertificates
 * @since 4.1.1
 */
public class CurrentClientCertificatesHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String ATTRIBUTE_CLIENT_CERTIFICATES = "jakarta.servlet.request.X509Certificate";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentClientCertificates.class)
                && List.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        var servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            return List.of();
        }

        var certificates = (X509Certificate[]) servletRequest.getAttribute(ATTRIBUTE_CLIENT_CERTIFICATES);
        if (ArrayUtils.isEmpty(certificates)) {
            return List.of();
        }
        return Arrays.asList(certificates);
    }
}
