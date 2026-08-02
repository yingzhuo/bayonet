package com.github.yingzhuo.bayonet.toml;

import com.github.yingzhuo.bayonet.context.AbstractPropertySourceFactory;

/**
 * TOML 配置文件的 {@link org.springframework.core.io.support.PropertySourceFactory} 实现。
 * <p>用于 {@code @PropertySource(factory = TomlPropertySourceFactory.class)}，
 * 支持加载 {@code .toml} 格式的 TOML 配置文件。</p>
 *
 * <pre>{@code
 * @PropertySource(factory = TomlPropertySourceFactory.class, value = "classpath:application.toml")
 * @Configuration
 * public class MyConfig { }
 * }</pre>
 *
 * @author 应卓
 * @see TomlPropertySourceLoader
 * @see AbstractPropertySourceFactory
 * @since 4.1.1
 */
public class TomlPropertySourceFactory extends AbstractPropertySourceFactory {

    public TomlPropertySourceFactory() {
        super(new TomlPropertySourceLoader());
    }
}
