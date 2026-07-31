package com.github.yingzhuo.bayonet.security.filter;

import com.github.yingzhuo.bayonet.log.LogLevel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求日志过滤器。
 * <p>记录 HTTP 请求的方法、路径、参数和头部信息。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class LoggingFilter extends OncePerRequestFilter {

    private final LogLevel logLevel;

    private Set<String> sensitiveHeaders = Set.of(HttpHeaders.COOKIE, HttpHeaders.SET_COOKIE, "X-Api-Key");

    /**
     * 构造器 (Debug级别)
     */
    public LoggingFilter() {
        this(LogLevel.debug(LoggingFilter.class));
    }

    /**
     * 构造器
     *
     * @param logLevel 日志级别
     */
    public LoggingFilter(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    private static String getRequestPath(HttpServletRequest request) {
        var uri = request.getRequestURI();
        var query = request.getQueryString();
        return query != null ? uri + "?" + query : uri;
    }

    private static Map<String, String> getParams(HttpServletRequest request) {
        var params = request.getParameterMap();
        if (params.isEmpty()) {
            return Map.of();
        }
        var result = new LinkedHashMap<String, String>();
        for (var entry : params.entrySet()) {
            result.put(entry.getKey(), String.join(",", entry.getValue()));
        }
        return Map.copyOf(result);
    }

    /**
     * 设置敏感头部。
     * <p>匹配到的头部不会出现在日志中，匹配不区分大小写。</p>
     *
     * @param sensitiveHeaders 敏感头部名称集合
     */
    public void setSensitiveHeaders(@Nullable Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders != null
                ? Set.copyOf(sensitiveHeaders)
                : Set.of();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (logLevel.isEnabled()) {
            logLevel.log("{} {} | params: {} | headers: {}",
                    request.getMethod(),
                    getRequestPath(request),
                    getParams(request),
                    getHeaders(request));
        }

        filterChain.doFilter(request, response);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        var names = request.getHeaderNames();
        if (names == null || !names.hasMoreElements()) {
            return Map.of();
        }
        var result = new LinkedHashMap<String, String>();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            if (!this.sensitiveHeaders.contains(name.toLowerCase())) {
                result.put(name, request.getHeader(name));
            }
        }
        return result;
    }
}
