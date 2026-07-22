package com.github.yingzhuo.bayonet.webmvc.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Objects;

/**
 * 客户端远程地址工具类。
 * <p>从 HTTP 请求头中提取客户端真实 IP 地址，依次检查
 * {@code X-Forwarded-For}、{@code Proxy-Client-IP}、{@code WL-Proxy-Client-IP}、
 * {@code HTTP_CLIENT_IP}、{@code HTTP_X_FORWARDED_FOR}，
 * 最后回退到 {@link HttpServletRequest#getRemoteAddr()}。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemoteAddressUtils {

    /**
     * 获取客户端 IP 地址。
     *
     * @param request HttpServletRequest
     * @return IP 地址，请求为 {@code null} 时返回 {@code null}
     */
    @Nullable
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        var ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            var ips = ip.split(",");
            for (var strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 获取客户端 IP 地址（必须存在）。
     *
     * @param request HttpServletRequest
     * @return IP 地址
     * @throws NullPointerException 请求为 {@code null} 或 IP 获取失败时抛出
     */
    public static String getRequiredIpAddress(HttpServletRequest request) {
        var ip = getIpAddress(request);
        return Objects.requireNonNull(ip);
    }

    /**
     * 获取客户端 IP 地址（{@link NativeWebRequest} 版本）。
     *
     * @param request NativeWebRequest
     * @return IP 地址，无底层请求时返回 {@code null}
     */
    @Nullable
    public static String getIpAddress(NativeWebRequest request) {
        if (request == null) {
            return null;
        }
        var httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
        return httpServletRequest != null ? getIpAddress(httpServletRequest) : null;
    }

    /**
     * 获取客户端 IP 地址（{@link NativeWebRequest} 版本，必须存在）。
     *
     * @param request NativeWebRequest
     * @return IP 地址
     * @throws NullPointerException 无底层请求或 IP 获取失败时抛出
     */
    public static String getRequiredIpAddress(NativeWebRequest request) {
        var ip = getIpAddress(request);
        return Objects.requireNonNull(ip);
    }

}
