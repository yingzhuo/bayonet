package com.github.yingzhuo.bayonet.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * 认证详情创建器接口。
 * <p>用于创建认证详情对象，通常通过 {@link Authentication#getDetails()} 获取认证详情。
 * 默认实现基于 {@link WebAuthenticationDetailsSource}，从请求中提取远程地址与会话 ID，
 * 生成 {@link WebAuthenticationDetails}。</p>
 *
 * <pre>{@code
 * var creator = AuthenticationDetailsCreator.createDefault();
 * var details = creator.create(webRequest);
 * authentication.setDetails(details);
 * }</pre>
 *
 * @author 应卓
 * @see WebAuthenticationDetailsSource
 * @see WebAuthenticationDetails
 * @since 4.1.1
 */
public interface AuthenticationDetailsCreator {

    /**
     * 创建默认的 {@link AuthenticationDetailsCreator} 实例。
     *
     * @return 默认实现
     */
    static AuthenticationDetailsCreator createDefault() {
        return new Default();
    }

    /**
     * 根据请求创建认证详情对象。
     *
     * @param request 请求（非 {@code null}）
     * @return 认证详情对象（非 {@code null}）
     */
    Object create(NativeWebRequest request);

    // ------

    /**
     * 默认实现。
     * <p>通过 {@link WebAuthenticationDetailsSource} 从请求中提取远程地址与会话 ID，
     * 生成 {@link WebAuthenticationDetails}。</p>
     */
    class Default implements AuthenticationDetailsCreator {

        private static final WebAuthenticationDetailsSource DETAILS_SOURCE = new WebAuthenticationDetailsSource();

        @Override
        public Object create(NativeWebRequest request) {
            var httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
            Assert.state(httpServletRequest != null, "httpServletRequest must not be null");
            return DETAILS_SOURCE.buildDetails(httpServletRequest);
        }
    }
}
