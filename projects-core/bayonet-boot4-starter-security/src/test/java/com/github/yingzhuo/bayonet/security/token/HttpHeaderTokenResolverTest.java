package com.github.yingzhuo.bayonet.security.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpHeaderTokenResolverTest {

    private static final String HEADER = "X-Auth-Token";

    @Mock
    private WebRequest webRequest;

    // ============== 构造器 ==============

    @Test
    void should_create_when_headerNameValid() {
        assertThat(new HttpHeaderTokenResolver("X-Custom")).isNotNull();
    }

    @Test
    void should_create_with_prefix() {
        assertThat(new HttpHeaderTokenResolver(HEADER, "Bearer ")).isNotNull();
    }

    @Test
    void should_create_with_nullPrefix() {
        assertThat(new HttpHeaderTokenResolver(HEADER, null)).isNotNull();
    }

    @Test
    void should_throw_when_headerNameIsNull() {
        assertThatThrownBy(() -> new HttpHeaderTokenResolver(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_headerNameIsEmpty() {
        assertThatThrownBy(() -> new HttpHeaderTokenResolver(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== resolve 无前缀 ==============

    @Test
    void should_return_token_when_headerPresent() {
        when(webRequest.getHeader(HEADER)).thenReturn("mytoken");
        assertThat(new HttpHeaderTokenResolver(HEADER).resolve(webRequest)).isEqualTo("mytoken");
    }

    @Test
    void should_trim_token_when_headerHasSpaces() {
        when(webRequest.getHeader(HEADER)).thenReturn("  token123  ");
        assertThat(new HttpHeaderTokenResolver(HEADER).resolve(webRequest)).isEqualTo("token123");
    }

    @Test
    void should_return_null_when_headerMissing() {
        when(webRequest.getHeader(HEADER)).thenReturn(null);
        assertThat(new HttpHeaderTokenResolver(HEADER).resolve(webRequest)).isNull();
    }

    @Test
    void should_return_null_when_headerEmpty() {
        when(webRequest.getHeader(HEADER)).thenReturn("");
        assertThat(new HttpHeaderTokenResolver(HEADER).resolve(webRequest)).isNull();
    }

    // ============== resolve 带前缀 ==============

    @Test
    void should_return_token_when_prefixMatches() {
        when(webRequest.getHeader(HEADER)).thenReturn("Bearer token123");
        assertThat(new HttpHeaderTokenResolver(HEADER, "Bearer ").resolve(webRequest)).isEqualTo("token123");
    }

    @Test
    void should_trim_token_when_prefixMatches_withTrailingSpaces() {
        when(webRequest.getHeader(HEADER)).thenReturn("Bearer token123  ");
        assertThat(new HttpHeaderTokenResolver(HEADER, "Bearer ").resolve(webRequest)).isEqualTo("token123");
    }

    @Test
    void should_return_null_when_prefixNotMatch() {
        when(webRequest.getHeader(HEADER)).thenReturn("Basic dXNlcjpwYXNz");
        assertThat(new HttpHeaderTokenResolver(HEADER, "Bearer ").resolve(webRequest)).isNull();
    }

    @Test
    void should_return_null_when_prefixCaseMismatch() {
        when(webRequest.getHeader(HEADER)).thenReturn("bearer token123");
        assertThat(new HttpHeaderTokenResolver(HEADER, "Bearer ").resolve(webRequest)).isNull();
    }

}
