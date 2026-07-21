package com.github.yingzhuo.bayonet.webcli.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Bearer Token 认证请求拦截器。
 *
 * <p>在请求头中添加 {@code Authorization: Bearer &lt;token&gt;}。
 * 若请求已包含 {@code Authorization} 头，则不会覆盖已有值。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * var interceptor = new BearerAuthClientHttpRequestInterceptor("my-jwt-token");
 *
 * RestTemplate restTemplate = new RestTemplate();
 * restTemplate.setInterceptors(List.of(interceptor));
 * }</pre>
 *
 * @author 应卓
 * @see ClientHttpRequestInterceptor
 * @see com.github.yingzhuo.bayonet.webcli.util.InterceptorFactories
 * @since 4.1.0
 */
public class BearerAuthClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String token;

    public BearerAuthClientHttpRequestInterceptor(String token) {
        Assert.hasText(token, "token must not be empty");
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        var headers = request.getHeaders();
        if (!headers.containsHeader(HttpHeaders.AUTHORIZATION)) {
            headers.setBearerAuth(token);
        }
        return execution.execute(request, body);
    }
}
