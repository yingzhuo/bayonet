package com.github.yingzhuo.bayonet.webcli.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * User-Agent 请求头拦截器。
 * <p>为 HTTP 请求设置 {@code User-Agent} 请求头；若请求已显式指定 User-Agent 则跳过。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class UserAgentClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String userAgentValue;

    /**
     * 构造器
     *
     * @param userAgentValue User-Agent 值
     */
    public UserAgentClientHttpRequestInterceptor(String userAgentValue) {
        Assert.hasText(userAgentValue, "userAgentValue must not be empty");
        this.userAgentValue = userAgentValue;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        var headers = request.getHeaders();
        if (!headers.containsHeader(HttpHeaders.USER_AGENT)) {
            headers.set(HttpHeaders.USER_AGENT, userAgentValue);
        }
        return execution.execute(request, body);
    }
}
