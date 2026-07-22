package com.github.yingzhuo.bayonet.security.mvc;

import com.github.yingzhuo.bayonet.security.filter.TokenBasedAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * 解析 {@link CurrentToken @CurrentToken} 注解参数的 {@link HandlerMethodArgumentResolver}。
 *
 * <p>从 {@link TokenBasedAuthenticationFilter} 存储在 request attribute 中的 token 取值，
 * 支持 {@link String} 和 {@link Optional Optional&lt;String&gt;} 两种参数类型。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @GetMapping("/me")
 * public String me(@CurrentToken String token) {
 *     return token;
 * }
 *
 * @GetMapping("/optional")
 * public String optional(@CurrentToken Optional<String> tokenOpt) {
 *     return tokenOpt.orElse("anonymous");
 * }
 * }</pre>
 *
 * @author 应卓
 * @see CurrentToken
 * @see TokenBasedAuthenticationFilter
 * @since 4.1.1
 */
public class CurrentTokenHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentToken.class) &&
                (String.class.isAssignableFrom(parameter.getParameterType()) || Optional.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        var servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            return resolveEmpty(parameter);
        }

        var token = servletRequest.getAttribute(TokenBasedAuthenticationFilter.ATTRIBUTE_TOKEN_NAME);
        if (String.class.isAssignableFrom(parameter.getParameterType())) {
            return token;
        }
        return Optional.ofNullable(token);
    }

    @Nullable
    private static Object resolveEmpty(MethodParameter parameter) {
        if (String.class.isAssignableFrom(parameter.getParameterType())) {
            return null;
        }
        return Optional.empty();
    }

}
