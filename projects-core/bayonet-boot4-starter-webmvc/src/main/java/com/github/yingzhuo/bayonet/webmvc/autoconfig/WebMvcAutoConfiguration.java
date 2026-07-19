package com.github.yingzhuo.bayonet.webmvc.autoconfig;

import com.github.yingzhuo.bayonet.webmvc.support.arg.RemoteAddressHandlerMethodArgumentResolver;
import com.github.yingzhuo.bayonet.webmvc.support.ret.AttachmentRetHandlerMethodReturnValueHandler;
import com.github.yingzhuo.bayonet.webmvc.support.ret.ImageRetHandlerMethodReturnValueHandler;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    @Autowired(required = false)
    public void configBeanNameViewResolver(@Nullable BeanNameViewResolver resolver) {
        if (resolver != null) {
            resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RemoteAddressHandlerMethodArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new AttachmentRetHandlerMethodReturnValueHandler());
        handlers.add(new ImageRetHandlerMethodReturnValueHandler());
    }
}
