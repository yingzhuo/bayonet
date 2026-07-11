package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceLoaderUtilsTest {

    // ============== 默认加载 ==============

    @Test
    void should_load_from_defaultClassLoader() {
        var services = ServiceLoaderUtils.load(SpiService.class).toList();
        assertThat(services).isNotEmpty();
        assertThat(services.get(0)).isInstanceOf(SpiServiceImpl.class);
    }

    // ============== 自定义 classLoader ==============

    @Test
    void should_load_with_customClassLoader() {
        var services = ServiceLoaderUtils.load(
                SpiService.class,
                Thread.currentThread().getContextClassLoader()
        ).toList();
        assertThat(services).isNotEmpty();
    }

    @Test
    void should_useDefault_when_classLoaderIsNull() {
        var services = ServiceLoaderUtils.load(SpiService.class, (ClassLoader) null).toList();
        assertThat(services).isNotEmpty();
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_targetTypeIsNull() {
        assertThatThrownBy(() -> ServiceLoaderUtils.load(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_targetTypeIsNull_withClassLoader() {
        assertThatThrownBy(() -> ServiceLoaderUtils.load(null, Thread.currentThread().getContextClassLoader()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 类型不匹配 ==============

    @Test
    void should_return_empty_when_noServicesFound() {
        // String 没有 SPI 实现
        var services = ServiceLoaderUtils.load(String.class).toList();
        assertThat(services).isEmpty();
    }

    // ============== SPI 测试类型 ==============

    public interface SpiService {
        String getName();
    }

    public static class SpiServiceImpl implements SpiService {
        @Override
        public String getName() {
            return "test";
        }
    }

}
