package com.github.yingzhuo.bayonet.security.event;

import org.springframework.web.context.request.WebRequest;

/**
 * Token 解析成功事件。
 * <p>在 Token 被成功从请求中提取后发布，此时尚未进行认证。</p>
 *
 * @param currentRequest 当前 Web 请求
 * @param token          提取到的原始 Token 字符串
 * @author 应卓
 */
public record TokenResolvedEvent(WebRequest currentRequest, String token) {
}
