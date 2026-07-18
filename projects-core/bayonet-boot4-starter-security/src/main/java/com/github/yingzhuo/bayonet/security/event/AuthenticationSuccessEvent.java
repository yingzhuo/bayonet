package com.github.yingzhuo.bayonet.security.event;

import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.WebRequest;

/**
 * 认证成功事件。
 * <p>Token 认证通过后发布，此时 SecurityContext 已设置认证信息。</p>
 *
 * @param currentRequest 当前 Web 请求
 * @param token          认证使用的原始 Token 字符串
 * @param authentication 认证通过的 Authentication 实例
 * @author 应卓
 */
public record AuthenticationSuccessEvent(WebRequest currentRequest, String token, Authentication authentication) {
}
