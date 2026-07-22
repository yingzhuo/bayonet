package com.github.yingzhuo.bayonet.security.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerHeaderTokenResolverTest {

    private final BearerHeaderTokenResolver resolver = new BearerHeaderTokenResolver();
    @Mock
    private WebRequest webRequest;

    @Test
    void should_return_token_when_bearerPresent() {
        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer mytoken");
        assertThat(resolver.resolve(webRequest)).isEqualTo("mytoken");
    }

    @Test
    void should_trim_token_when_bearerHasTrailingSpaces() {
        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer mytoken  ");
        assertThat(resolver.resolve(webRequest)).isEqualTo("mytoken");
    }

    @Test
    void should_return_null_when_headerMissing() {
        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        assertThat(resolver.resolve(webRequest)).isNull();
    }

    @Test
    void should_return_null_when_notBearer() {
        when(webRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");
        assertThat(resolver.resolve(webRequest)).isNull();
    }

}
