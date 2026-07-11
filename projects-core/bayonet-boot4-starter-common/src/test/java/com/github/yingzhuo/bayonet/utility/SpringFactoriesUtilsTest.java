package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ProtocolResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringFactoriesUtilsTest {

    // ============== 默认加载 ==============

    @Test
    void should_load_from_defaultLocation() {
        // ProtocolResolver 在项目的 spring.factories 中有 2 个实现
        var result = SpringFactoriesUtils.load(ProtocolResolver.class).toList();
        assertThat(result).isNotEmpty();
    }

    // ============== 自定义 resourceLocation ==============

    @Test
    void should_return_empty_when_locationNotExists() {
        var result = SpringFactoriesUtils.load(
                ProtocolResolver.class,
                "META-INF/nonexistent-factories.properties"
        ).toList();
        assertThat(result).isEmpty();
    }

    @Test
    void should_useDefault_when_locationIsNull() {
        var result = SpringFactoriesUtils.load(ProtocolResolver.class, (String) null).toList();
        assertThat(result).isNotEmpty();
    }

    @Test
    void should_useDefault_when_locationIsEmpty() {
        var result = SpringFactoriesUtils.load(ProtocolResolver.class, "").toList();
        assertThat(result).isNotEmpty();
    }

    // ============== 自定义 classLoader ==============

    @Test
    void should_load_with_customClassLoader() {
        var result = SpringFactoriesUtils.load(
                ProtocolResolver.class,
                null,
                Thread.currentThread().getContextClassLoader()
        ).toList();
        assertThat(result).isNotEmpty();
    }

    @Test
    void should_useDefault_when_classLoaderIsNull() {
        var result = SpringFactoriesUtils.load(
                ProtocolResolver.class,
                null,
                (ClassLoader) null
        ).toList();
        assertThat(result).isNotEmpty();
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_targetTypeIsNull() {
        assertThatThrownBy(() -> SpringFactoriesUtils.load(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_targetTypeIsNull_withLocation() {
        assertThatThrownBy(() -> SpringFactoriesUtils.load(null, "META-INF/test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_targetTypeIsNull_withClassLoader() {
        assertThatThrownBy(() -> SpringFactoriesUtils.load(null, null, (ClassLoader) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
