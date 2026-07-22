package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.security.event.AuthenticationFailureEvent;
import com.github.yingzhuo.bayonet.security.event.AuthenticationSuccessEvent;
import com.github.yingzhuo.bayonet.security.event.TokenResolvedEvent;
import com.github.yingzhuo.bayonet.security.token.TokenConverter;
import com.github.yingzhuo.bayonet.security.token.TokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBasedAuthenticationFilterTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final TokenBasedAuthenticationFilter<UsernamePasswordAuthenticationToken> filter = new TokenBasedAuthenticationFilter<>() {
    };
    @Mock
    private TokenResolver tokenResolver;
    @Mock
    private TokenConverter<UsernamePasswordAuthenticationToken> tokenConverter;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RememberMeServices rememberMeServices;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Captor
    private ArgumentCaptor<Object> eventCaptor;

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

    // ============== RememberMeServices ==============

    @Test
    void should_invoke_rememberMeLoginSuccess_on_authSuccess() throws Exception {
        filter.setRememberMeServices(rememberMeServices);

        var auth = UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of());
        when(tokenResolver.resolve(any())).thenReturn("valid-token");
        when(tokenConverter.convert("valid-token")).thenReturn(auth);

        filter.doFilterInternal(request, response, filterChain);

        verify(rememberMeServices).loginSuccess(request, response, auth);
    }

    @Test
    void should_invoke_rememberMeLoginFail_on_authFailure() throws Exception {
        filter.setRememberMeServices(rememberMeServices);

        when(tokenResolver.resolve(any())).thenReturn("bad-token");
        when(tokenConverter.convert("bad-token")).thenThrow(new AuthenticationCredentialsNotFoundException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(rememberMeServices).loginFail(request, response);
    }

    @Test
    void should_notInvoke_rememberMe_when_notSet() throws Exception {
        when(tokenResolver.resolve(any())).thenReturn("valid-token");
        when(tokenConverter.convert("valid-token")).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of())
        );

        filter.doFilterInternal(request, response, filterChain);

        verify(rememberMeServices, never()).loginSuccess(any(), any(), any());
        verify(rememberMeServices, never()).loginFail(any(), any());
    }

    // ============== ApplicationEventPublisher ==============

    @Test
    void should_publish_TokenResolvedEvent_and_AuthenticationSuccessEvent_on_success() throws Exception {
        filter.setApplicationEventPublisher(eventPublisher);

        var auth = UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of());
        when(tokenResolver.resolve(any())).thenReturn("my-token");
        when(tokenConverter.convert("my-token")).thenReturn(auth);

        filter.doFilterInternal(request, response, filterChain);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());

        var events = eventCaptor.getAllValues();
        assertThat(events).hasSize(2);
        assertThat(events.get(0))
                .isInstanceOf(TokenResolvedEvent.class)
                .extracting(e -> ((TokenResolvedEvent) e).token())
                .isEqualTo("my-token");
        assertThat(events.get(1))
                .isInstanceOf(AuthenticationSuccessEvent.class)
                .extracting(e -> ((AuthenticationSuccessEvent) e).token())
                .isEqualTo("my-token");
    }

    @Test
    void should_publish_AuthenticationFailureEvent_on_authFailure() throws Exception {
        filter.setApplicationEventPublisher(eventPublisher);

        when(tokenResolver.resolve(any())).thenReturn("bad-token");
        when(tokenConverter.convert("bad-token")).thenThrow(new AuthenticationCredentialsNotFoundException("bad"));

        filter.doFilterInternal(request, response, filterChain);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());

        var events = eventCaptor.getAllValues();
        assertThat(events.get(0)).isInstanceOf(TokenResolvedEvent.class);
        assertThat(events.get(1)).isInstanceOf(AuthenticationFailureEvent.class);
    }

    @Test
    void should_notPublish_events_when_publisherNotSet() throws Exception {
        when(tokenResolver.resolve(any())).thenReturn("valid-token");
        when(tokenConverter.convert("valid-token")).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of())
        );

        filter.doFilterInternal(request, response, filterChain);

        verify(eventPublisher, never()).publishEvent(any());
    }

}
