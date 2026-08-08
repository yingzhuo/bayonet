package com.github.yingzhuo.bayonet.security.annotation;

import java.lang.annotation.*;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 标记控制器方法参数以注入当前请求的客户端证书链。
 *
 * <p>与 {@link CurrentClientCertificatesHandlerMethodArgumentResolver} 配合使用，
 * 从 request attribute {@code jakarta.servlet.request.X509Certificate} 读取
 * Servlet 容器填充的客户端证书链，注入为 {@code List<X509Certificate>} 类型的参数。</p>
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
 * @see CurrentClientCertificatesHandlerMethodArgumentResolver
 * @see X509Certificate
 * @since 4.1.1
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentClientCertificates {
}
