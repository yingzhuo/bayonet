package com.github.yingzhuo.bayonet.security.autoconfig;

import com.github.yingzhuo.bayonet.security.configurer.AdditionalFilterConfig;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Security 过滤器链自动配置 DSL。
 * <p>自动发现并应用 {@link AdditionalFilterConfig} Bean，将声明的附加过滤器按指定位置
 * （{@link com.github.yingzhuo.bayonet.security.configurer.FilterPositionHint#AT AT} /
 * {@link com.github.yingzhuo.bayonet.security.configurer.FilterPositionHint#BEFORE BEFORE} /
 * {@link com.github.yingzhuo.bayonet.security.configurer.FilterPositionHint#AFTER AFTER}）
 * 添加到 Spring Security 过滤器链中。</p>
 *
 * <p>配合 {@link com.github.yingzhuo.bayonet.security.configurer.AdditionalSecurityFilter @AdditionalSecurityFilter}
 * 注解使用，用户通过声明式注解注册过滤器，无需手动操作 {@link HttpSecurity}。</p>
 *
 * @author 应卓
 * @see AdditionalFilterConfig
 * @see com.github.yingzhuo.bayonet.security.configurer.AdditionalSecurityFilter
 * @see com.github.yingzhuo.bayonet.security.configurer.FilterPositionHint
 * @since 4.1.1
 */
@Slf4j
public class SecurityFilterAutoDSL extends AbstractHttpConfigurer<SecurityFilterAutoDSL, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) {
        var applicationContext = http.getSharedObject(ApplicationContext.class);
        if (applicationContext == null) {
            return;
        }

        applicationContext.getBeansOfType(AdditionalFilterConfig.class)
                .values()
                .forEach(conf -> {
                    var filter = getFilterBean(applicationContext, conf.filterType());
                    if (filter == null) {
                        return;
                    }
                    var positionFilterType = conf.positionFilterType();
                    var hint = conf.hint();

                    switch (hint) {
                        case AT -> http.addFilterAt(filter, positionFilterType);
                        case BEFORE -> http.addFilterBefore(filter, positionFilterType);
                        case AFTER -> http.addFilterAfter(filter, positionFilterType);
                    }
                });
    }

    @Nullable
    private Filter getFilterBean(ApplicationContext applicationContext, Class<? extends Filter> filterType) {
        try {
            return applicationContext.getBeanProvider(filterType).getObject();
        } catch (NoSuchBeanDefinitionException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }
}
