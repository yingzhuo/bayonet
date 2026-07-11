package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class SpringUtilsTest {

    @Test
    void should_return_context_after_springBootStartup() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .run();
        try {
            var result = SpringUtils.getApplicationContext();
            assertThat(result).isSameAs(ctx);
        } finally {
            ctx.close();
        }
    }

    @Test
    void should_getBean_after_startup() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .run();
        try {
            var bean = SpringUtils.getBean(Config.class);
            assertThat(bean).isNotNull();
        } finally {
            ctx.close();
        }
    }

    @Test
    void should_getBeanProvider_after_startup() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .run();
        try {
            var provider = SpringUtils.getBeanProvider(Config.class);
            assertThat(provider.getIfAvailable()).isNotNull();
        } finally {
            ctx.close();
        }
    }

    @Configuration
    static class Config {
    }

}
