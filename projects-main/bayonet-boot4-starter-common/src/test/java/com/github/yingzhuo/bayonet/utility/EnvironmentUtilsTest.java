package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Profiles;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentUtilsTest {

    // ============== 冷启动模式（无 Spring 上下文） ==============

    @Test
    void should_return_StandardEnvironment_when_noSpringContext() {
        assertThat(EnvironmentUtils.getEnvironment()).isNotNull();
    }

    @Test
    void should_resolvePlaceholders_when_noSpringContext() {
        var result = EnvironmentUtils.resolvePlaceholders("${java.version:unknown}");
        assertThat(result).isNotEqualTo("${java.version:unknown}");
    }

    @Test
    void should_returnProperty_when_keyExists() {
        var value = EnvironmentUtils.getProperty("java.version");
        assertThat(value).isNotNull();
    }

    @Test
    void should_returnNull_when_keyMissing() {
        var value = EnvironmentUtils.getProperty("nonexistent.property.key");
        assertThat(value).isNull();
    }

    @Test
    void should_returnTypedProperty_when_keyExists() {
        var value = EnvironmentUtils.getProperty("java.version", String.class);
        assertThat(value).isNotNull();
    }

    @Test
    void should_returnNull_when_typedKeyMissing() {
        var value = EnvironmentUtils.getProperty("nonexistent.key", String.class);
        assertThat(value).isNull();
    }

    @Test
    void should_returnDefault_when_typedKeyMissing() {
        var value = EnvironmentUtils.getProperty("nonexistent.key", String.class, "fallback");
        assertThat(value).isEqualTo("fallback");
    }

    @Test
    void should_returnValue_when_typedKeyExists() {
        var value = EnvironmentUtils.getProperty("java.version", String.class, "fallback");
        assertThat(value).isNotEqualTo("fallback");
    }

    @Test
    void should_returnDefault_when_stringKeyMissing() {
        var value = EnvironmentUtils.getProperty("nonexistent.key", "fallback");
        assertThat(value).isEqualTo("fallback");
    }

    @Test
    void should_returnValue_when_stringKeyExists() {
        var value = EnvironmentUtils.getProperty("java.version", "fallback");
        assertThat(value).isNotEqualTo("fallback");
    }

    // ============== Spring 集成模式 ==============

    @Test
    void should_acceptProfiles_when_springContextRunning() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .properties("spring.profiles.active=test")
                .run();
        try {
            var result = EnvironmentUtils.acceptsProfiles(Profiles.of("test"));
            assertThat(result).isTrue();
        } finally {
            ctx.close();
        }
    }

    @Test
    void should_rejectProfiles_when_notActive() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .properties("spring.profiles.active=prod")
                .run();
        try {
            var result = EnvironmentUtils.acceptsProfiles(Profiles.of("dev"));
            assertThat(result).isFalse();
        } finally {
            ctx.close();
        }
    }

    // ============== resolvePlaceholders ==============

    @Test
    void should_resolvePlaceholders_when_springContextRunning() {
        var ctx = new SpringApplicationBuilder(Config.class)
                .web(WebApplicationType.NONE)
                .properties("app.test.property=greeting")
                .run();
        try {
            var result = EnvironmentUtils.resolvePlaceholders("${app.test.property}");
            assertThat(result).isEqualTo("greeting");
        } finally {
            ctx.close();
        }
    }

    @Test
    void should_keepUnresolvedPlaceholders_when_notFound() {
        assertThat(EnvironmentUtils.resolvePlaceholders("${unknown.property:}")).isEmpty();
    }

    // ------

    @Configuration
    static class Config {
    }

}
