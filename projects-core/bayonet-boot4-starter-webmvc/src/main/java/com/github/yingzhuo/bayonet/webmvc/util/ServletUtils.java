package com.github.yingzhuo.bayonet.webmvc.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * Servlet 工具类。
 * <p>提供从 {@link RequestContextHolder} 获取当前请求、响应、会话等对象的方法。
 * 当无请求上下文时，{@code getXxx()} 系列方法返回 {@code null}，{@code getRequiredXxx()} 系列方法抛出异常。</p>
 *
 * @author 应卓
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletUtils {

    /**
     * 获取当前请求对象。
     *
     * @return HttpServletRequest，无请求上下文时返回 {@code null}
     */
    @Nullable
    public static HttpServletRequest getRequest() {
        try {
            var attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取当前请求对象（必须存在）。
     *
     * @return HttpServletRequest
     * @throws NullPointerException 无请求上下文时抛出
     */
    public static HttpServletRequest getRequiredRequest() {
        var request = getRequest();
        return Objects.requireNonNull(request);
    }

    /**
     * 获取解包后的当前请求对象。
     * <p>如果当前请求被 {@link HttpServletRequestWrapper} 包装，递归解包直至原始请求。</p>
     *
     * @return 解包后的 HttpServletRequest
     */
    public static HttpServletRequest getUnwrappedRequest() {
        var request = getRequiredRequest();
        while (request instanceof HttpServletRequestWrapper wrapper) {
            request = (HttpServletRequest) wrapper.getRequest();
        }
        return request;
    }

    /**
     * 获取当前响应对象。
     *
     * @return HttpServletResponse，无请求上下文时返回 {@code null}
     */
    @Nullable
    public static HttpServletResponse getResponse() {
        try {
            var attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getResponse();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取当前响应对象（必须存在）。
     *
     * @return HttpServletResponse
     * @throws NullPointerException 无请求上下文时抛出
     */
    public static HttpServletResponse getRequiredResponse() {
        var response = getResponse();
        return Objects.requireNonNull(response);
    }

    /**
     * 获取解包后的当前响应对象。
     * <p>如果当前响应被 {@link HttpServletResponseWrapper} 包装，递归解包直至原始响应。</p>
     *
     * @return 解包后的 HttpServletResponse
     */
    public static HttpServletResponse getUnwrappedResponse() {
        var response = getRequiredResponse();
        while (response instanceof HttpServletResponseWrapper wrapper) {
            response = (HttpServletResponse) wrapper.getResponse();
        }
        return response;
    }

    /**
     * 获取当前会话（不存在时创建）。
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        return getSession(true);
    }

    /**
     * 获取当前会话。
     *
     * @param create 不存在时是否创建
     * @return HttpSession
     */
    public static HttpSession getSession(boolean create) {
        return getRequiredRequest().getSession(create);
    }

    /**
     * 获取当前会话 ID。
     *
     * @return 会话 ID
     */
    public static String getSessionId() {
        return getSession().getId();
    }

    /**
     * 获取 ServletContext。
     *
     * @return ServletContext
     */
    public static ServletContext getServletContext() {
        return getRequiredRequest().getServletContext();
    }

}
