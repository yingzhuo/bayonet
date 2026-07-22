package com.github.yingzhuo.bayonet.hocon;

/**
 * HOCON 配置文件的 {@link org.springframework.core.io.support.PropertySourceFactory} 实现。
 * <p>用于 {@code @PropertySource(factory = HoconPropertySourceFactory.class)}，
 * 支持加载 {@code .conf} 格式的 HOCON 配置文件。</p>
 *
 * <pre>{@code
 * @PropertySource(factory = HoconPropertySourceFactory.class, value = "classpath:application.conf")
 * @Configuration
 * public class MyConfig { }
 * }</pre>
 *
 * @author 应卓
 * @see HoconPropertySourceLoader
 * @see AbstractPropertySourceFactory
 * @since 4.1.0
 */
public class HoconPropertySourceFactory extends AbstractPropertySourceFactory {

    public HoconPropertySourceFactory() {
        super(new HoconPropertySourceLoader());
    }
}
