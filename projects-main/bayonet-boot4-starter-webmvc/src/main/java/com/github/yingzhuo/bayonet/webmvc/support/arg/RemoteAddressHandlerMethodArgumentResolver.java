package com.github.yingzhuo.bayonet.webmvc.support.arg;

import com.github.yingzhuo.bayonet.webmvc.util.RemoteAddressUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * {@link RemoteAddress} 注解的参数解析器。
 * <p>将 {@code @RemoteAddress} 注解的控制器方法参数自动注入为客户端 IP 地址。
 * 支持 {@link String} 和 {@link Optional Optional&lt;String&gt;} 两种参数类型。</p>
 *
 * @author 应卓
 * @since 4.1.0
 */
public class RemoteAddressHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RemoteAddress.class) &&
                parameter.getParameterType() == String.class;
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {

        try {
            return doResolveArgument(parameter, webRequest);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private Object doResolveArgument(MethodParameter parameter, NativeWebRequest webRequest) {
        var request = webRequest.getNativeRequest(HttpServletRequest.class);

        var ip = request != null
                ? RemoteAddressUtils.getIpAddress(request)
                : null;

        if (parameter.getParameterType() == String.class) {
            return ip;
        } else if (parameter.getParameterType() == Optional.class) {
            return Optional.ofNullable(ip);
        } else {
            return null;
        }
    }

}
