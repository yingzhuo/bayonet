package com.github.yingzhuo.bayonet.security.event;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.WebRequest;

/**
 * 认证失败事件。
 * <p>Token 认证抛出 {@link AuthenticationException} 后发布，此时 SecurityContext 已清除。</p>
 *
 * @param currentRequest 当前 Web 请求
 * @param token          认证使用的原始 Token 字符串
 * @param e              认证异常信息
 * @author 应卓
 */
public record AuthenticationFailureEvent(WebRequest currentRequest, String token, AuthenticationException e) {
}
