package com.github.yingzhuo.bayonet.webcli.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;

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
