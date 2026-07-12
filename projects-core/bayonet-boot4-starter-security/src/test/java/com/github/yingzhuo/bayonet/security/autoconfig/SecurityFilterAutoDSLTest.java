package com.github.yingzhuo.bayonet.security.autoconfig;

import com.github.yingzhuo.bayonet.security.filtercfg.FilterConfigurer;
import com.github.yingzhuo.bayonet.security.filtercfg.FilterConfigurer.PositionHint;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityFilterAutoDSLTest {

    private HttpSecurity http;
    private SecurityFilterAutoDSL dsl;

    @BeforeEach
    void setUp() throws Exception {
        //noinspection Convert2Lambda
        ObjectPostProcessor<Object> postProcessor = new ObjectPostProcessor<>() {
            @Override
            public <O> O postProcess(O object) {
                return object;
            }
        };
        var authBuilder = new AuthenticationManagerBuilder(postProcessor);
        http = new HttpSecurity(postProcessor, authBuilder, new HashMap<>());
        http.authenticationManager(mock(AuthenticationManager.class));
        dsl = new SecurityFilterAutoDSL();
    }

    private DefaultSecurityFilterChain build() throws Exception {
        return http.build();
    }

    // ============== ApplicationContext 为 null ==============

    @Test
    void should_skip_when_applicationContextIsNull() throws Exception {
        assertThatCode(() -> dsl.configure(http))
                .doesNotThrowAnyException();
        build();
    }

    // ============== 无 FilterConfigurer Bean ==============

    @Test
    void should_doNothing_when_noFilterConfigurerBeans() throws Exception {
        var ctx = mock(ApplicationContext.class);
        when(ctx.getBeansOfType(FilterConfigurer.class)).thenReturn(Map.of());
        http.setSharedObject(ApplicationContext.class, ctx);

        dsl.configure(http);
        var chain = build();
        assertThat(chain.getFilters()).isEmpty();
    }

    // ============== BEFORE ==============

    @Test
    @SuppressWarnings("unchecked")
    void should_addFilterBefore_when_hintIsBefore() throws Exception {
        var ctx = mock(ApplicationContext.class);
        var configurer = mock(FilterConfigurer.class);
        var filter = mock(Filter.class);

        when(configurer.getFilter()).thenReturn(filter);
        when(configurer.getPositionHint()).thenReturn(PositionHint.BEFORE);
        when(configurer.getPositionFilterClass()).thenReturn((Class) UsernamePasswordAuthenticationFilter.class);
        when(ctx.getBeansOfType(FilterConfigurer.class)).thenReturn(Map.of("c", configurer));
        http.setSharedObject(ApplicationContext.class, ctx);

        dsl.configure(http);
        var chain = build();
        assertThat(chain.getFilters()).contains(filter);
    }

    // ============== AFTER ==============

    @Test
    @SuppressWarnings("unchecked")
    void should_addFilterAfter_when_hintIsAfter() throws Exception {
        var ctx = mock(ApplicationContext.class);
        var configurer = mock(FilterConfigurer.class);
        var filter = mock(Filter.class);

        when(configurer.getFilter()).thenReturn(filter);
        when(configurer.getPositionHint()).thenReturn(PositionHint.AFTER);
        when(configurer.getPositionFilterClass()).thenReturn((Class) UsernamePasswordAuthenticationFilter.class);
        when(ctx.getBeansOfType(FilterConfigurer.class)).thenReturn(Map.of("c", configurer));
        http.setSharedObject(ApplicationContext.class, ctx);

        dsl.configure(http);
        var chain = build();
        assertThat(chain.getFilters()).contains(filter);
    }

    // ============== AT ==============

    @Test
    @SuppressWarnings("unchecked")
    void should_addFilterAt_when_hintIsAt() throws Exception {
        var ctx = mock(ApplicationContext.class);
        var configurer = mock(FilterConfigurer.class);
        var filter = mock(Filter.class);

        when(configurer.getFilter()).thenReturn(filter);
        when(configurer.getPositionHint()).thenReturn(PositionHint.AT);
        when(configurer.getPositionFilterClass()).thenReturn((Class) UsernamePasswordAuthenticationFilter.class);
        when(ctx.getBeansOfType(FilterConfigurer.class)).thenReturn(Map.of("c", configurer));
        http.setSharedObject(ApplicationContext.class, ctx);

        dsl.configure(http);
        var chain = build();
        assertThat(chain.getFilters()).contains(filter);
    }

    // ============== 多配置器 ==============

    @Test
    @SuppressWarnings("unchecked")
    void should_handleMultipleConfigurers() throws Exception {
        var ctx = mock(ApplicationContext.class);
        var c1 = mock(FilterConfigurer.class);
        var c2 = mock(FilterConfigurer.class);
        var f1 = mock(Filter.class);
        var f2 = mock(Filter.class);

        when(c1.getFilter()).thenReturn(f1);
        when(c1.getPositionHint()).thenReturn(PositionHint.BEFORE);
        when(c1.getPositionFilterClass()).thenReturn((Class) UsernamePasswordAuthenticationFilter.class);
        when(c2.getFilter()).thenReturn(f2);
        when(c2.getPositionHint()).thenReturn(PositionHint.AFTER);
        when(c2.getPositionFilterClass()).thenReturn((Class) UsernamePasswordAuthenticationFilter.class);
        when(ctx.getBeansOfType(FilterConfigurer.class)).thenReturn(Map.of("c1", c1, "c2", c2));
        http.setSharedObject(ApplicationContext.class, ctx);

        dsl.configure(http);
        var chain = build();
        assertThat(chain.getFilters()).contains(f1, f2);
    }

}
