package com.github.yingzhuo.bayonet.security.autoconfig;

import com.github.yingzhuo.bayonet.security.filtercfg.FilterConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Security 过滤器自动配置 DSL。
 * <p>通过 {@code spring.factories} 自动注册，将容器中所有 {@link FilterConfigurer} 类型的 Bean
 * 按 {@link FilterConfigurer.PositionHint 定位提示} 添加到 Security 过滤器链中的指定位置。</p>
 *
 * @see FilterConfigurer
 * @author 应卓
 */
public class SecurityFilterAutoDSL extends AbstractHttpConfigurer<SecurityFilterAutoDSL, HttpSecurity> {

    @Override
    public void configure(final HttpSecurity http) {
        super.configure(http);

        var applicationContext = http.getSharedObject(ApplicationContext.class);
        if (applicationContext == null) { // 其实这里不会为null 大语言模型非要这里防止NPE
            return;
        }

        applicationContext.getBeansOfType(FilterConfigurer.class)
                .values()
                .forEach(c -> {
                    var filter = c.getFilter();
                    var positionHint = c.getPositionHint();
                    var positionFilterClz = c.getPositionFilterClass();

                    switch (positionHint) {
                        case BEFORE -> http.addFilterBefore(filter, positionFilterClz);
                        case AFTER -> http.addFilterAfter(filter, positionFilterClz);
                        case AT -> http.addFilterAt(filter, positionFilterClz);
                    }
                });
    }

}
