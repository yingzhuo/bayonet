package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.security.token.TokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBasedAuthenticationFilterTest {

    @Mock
    private TokenResolver tokenResolver;

    @Mock
    private TokenConverter<UsernamePasswordAuthenticationToken> tokenConverter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletResponse response;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final TokenBasedAuthenticationFilter<UsernamePasswordAuthenticationToken> filter = new TokenBasedAuthenticationFilter<>() {
    };

    @BeforeEach
    void setUp() {
        filter.setTokenResolver(tokenResolver);
        filter.setTokenConverter(tokenConverter);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ============== Assert.notNull 校验 ==============

    @Test
    void should_throw_when_securityContextHolderStrategyIsNull() {
        filter.setSecurityContextHolderStrategy(null);
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_tokenResolverIsNull() {
        filter.setTokenResolver(null);
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_tokenConverterIsNull() {
        filter.setTokenConverter(null);
        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 认证成功 ==============

    @Test
    void should_authenticate_when_tokenValid() throws Exception {
        var auth = UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of());
        when(tokenResolver.resolve(any())).thenReturn("valid-token");
        when(tokenConverter.convert("valid-token")).thenReturn(auth);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(auth);
    }

    // ============== token 解析失败 ==============

    @Test
    void should_skip_when_tokenResolverReturnsNull() throws Exception {
        when(tokenResolver.resolve(any())).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ============== convert 返回 null ==============

    @Test
    void should_skip_when_convertReturnsNull() throws Exception {
        when(tokenResolver.resolve(any())).thenReturn("token");
        when(tokenConverter.convert("token")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ============== convert 抛异常 ==============

    @Test
    void should_clearContext_when_convertThrowsAuthException() throws Exception {
        when(tokenResolver.resolve(any())).thenReturn("bad-token");
        when(tokenConverter.convert("bad-token")).thenThrow(new AuthenticationCredentialsNotFoundException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void should_invoke_entryPoint_when_convertThrowsAuthException() throws Exception {
        var entryPoint = mock(AuthenticationEntryPoint.class);
        filter.setAuthenticationEntryPoint(entryPoint);

        when(tokenResolver.resolve(any())).thenReturn("bad-token");
        when(tokenConverter.convert("bad-token")).thenThrow(new AuthenticationCredentialsNotFoundException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(entryPoint).commence(any(), any(), any(AuthenticationException.class));
    }

    @Test
    void should_continueChain_when_convertThrowsAuthException_withoutEntryPoint() throws Exception {
        filter.setAuthenticationEntryPoint(null);

        when(tokenResolver.resolve(any())).thenReturn("bad-token");
        when(tokenConverter.convert("bad-token")).thenThrow(new AuthenticationCredentialsNotFoundException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, atLeastOnce()).doFilter(any(), any());
    }

    // ============== authenticationIsRequired ==============

    @Test
    void should_skip_when_alreadyAuthenticated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("existing", null, java.util.List.of())
        );

        filter.doFilterInternal(request, response, filterChain);

        verify(tokenResolver, never()).resolve(any());
        verify(filterChain).doFilter(request, response);
    }

}
