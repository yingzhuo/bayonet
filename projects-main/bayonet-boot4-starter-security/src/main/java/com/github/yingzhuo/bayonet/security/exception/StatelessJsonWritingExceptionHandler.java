package com.github.yingzhuo.bayonet.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Objects;

/**
 * 无状态 JSON 异常处理器抽象基类。
 * <p>同时实现 {@link AuthenticationEntryPoint}、{@link AccessDeniedHandler} 与
 * {@link RequestRejectedHandler}，统一将认证失败（默认 401）、授权失败（默认 403）
 * 与请求被拒绝（默认 400）以 JSON 形式写入响应。
 * 子类仅需实现 {@link #handleAuthenticationException}、{@link #handleAccessDeniedException}
 * 与 {@link #handleRequestRejectedException} 三个抽象方法，生产具体的 JSON 内容
 * （通常为 {@link Object} 形式的错误体）。</p>
 *
 * <p>序列化基于 Jackson 3（{@link JsonMapper}）。若构造时未显式提供 {@link JsonMapper}，
 * 则使用默认实例。</p>
 *
 * <pre>{@code
 * public class MyExceptionHandler extends StatelessJsonWritingExceptionHandler {
 *
 *     @Override
 *     protected Object handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
 *         return Map.of("message", ex.getMessage());
 *     }
 *
 *     @Override
 *     protected Object handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
 *         return Map.of("message", ex.getMessage());
 *     }
 *
 *     @Override
 *     protected Object handleRequestRejectedException(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex) {
 *         return Map.of("message", ex.getMessage());
 *     }
 * }
 * }</pre>
 *
 * @author 应卓
 * @see AuthenticationEntryPoint
 * @see AccessDeniedHandler
 * @see RequestRejectedHandler
 * @since 4.1.1
 */
public abstract class StatelessJsonWritingExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler, RequestRejectedHandler {

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private final int fixedResponseCode;
    private final JsonMapper jsonMapper;

    /**
     * 构造器，使用默认 HTTP 状态码（认证失败 401、授权失败 403、请求被拒 400）。
     */
    protected StatelessJsonWritingExceptionHandler() {
        this(-1);
    }

    /**
     * 构造器，指定固定 HTTP 状态码。
     *
     * @param fixedResponseCode 固定响应状态码；{@code <= 0} 时使用默认状态码
     *                          （认证失败 {@code 401}、授权失败 {@code 403}、请求被拒 {@code 400}）
     */
    protected StatelessJsonWritingExceptionHandler(int fixedResponseCode) {
        this(fixedResponseCode, null);
    }

    /**
     * 构造器，指定固定 HTTP 状态码与 {@link JsonMapper}。
     *
     * @param fixedResponseCode 固定响应状态码；{@code <= 0} 时使用默认状态码
     *                          （认证失败 {@code 401}、授权失败 {@code 403}、请求被拒 {@code 400}）
     * @param jsonMapper        用于序列化 JSON 的 {@link JsonMapper}，为 {@code null} 时使用默认实例
     */
    protected StatelessJsonWritingExceptionHandler(int fixedResponseCode, @Nullable JsonMapper jsonMapper) {
        this.fixedResponseCode = fixedResponseCode;
        this.jsonMapper = Objects.requireNonNullElseGet(jsonMapper, JsonMapper::new);
    }

    @Override
    public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        var jsonObject = this.handleAuthenticationException(request, response, ex);
        response.setStatus(fixedResponseCode > 0 ? fixedResponseCode : HttpServletResponse.SC_UNAUTHORIZED); // 默认 401
        response.setContentType(CONTENT_TYPE);
        this.jsonMapper.writeValue(response.getOutputStream(), jsonObject);
        response.getOutputStream().flush();
    }

    @Override
    public final void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        var jsonObject = this.handleAccessDeniedException(request, response, ex);
        response.setStatus(fixedResponseCode > 0 ? fixedResponseCode : HttpServletResponse.SC_FORBIDDEN); // 默认 403
        response.setContentType(CONTENT_TYPE);
        this.jsonMapper.writeValue(response.getOutputStream(), jsonObject);
        response.getOutputStream().flush();
    }

    @Override
    public final void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        var jsonObject = this.handleRequestRejectedException(request, response, ex);
        response.setStatus(fixedResponseCode > 0 ? fixedResponseCode : HttpServletResponse.SC_BAD_REQUEST); // 默认 400
        response.setContentType(CONTENT_TYPE);
        this.jsonMapper.writeValue(response.getOutputStream(), jsonObject);
        response.getOutputStream().flush();
    }

    // ------

    /**
     * 处理认证失败，生产 JSON 内容。
     *
     * @param request  请求（非 {@code null}）
     * @param response 响应（非 {@code null}）
     * @param ex       认证异常（非 {@code null}）
     * @return 待序列化为 JSON 的对象（可为 {@code null}）
     */
    protected abstract Object handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex);

    /**
     * 处理授权失败，生产 JSON 内容。
     *
     * @param request  请求（非 {@code null}）
     * @param response 响应（非 {@code null}）
     * @param ex       授权异常（非 {@code null}）
     * @return 待序列化为 JSON 的对象（可为 {@code null}）
     */
    protected abstract Object handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex);

    /**
     * 处理请求被拒绝（防火墙拦截），生产 JSON 内容。
     *
     * @param request  请求（非 {@code null}）
     * @param response 响应（非 {@code null}）
     * @param ex       请求被拒绝异常（非 {@code null}）
     * @return 待序列化为 JSON 的对象（可为 {@code null}）
     */
    protected abstract Object handleRequestRejectedException(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex);

}
