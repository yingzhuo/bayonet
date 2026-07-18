package com.github.yingzhuo.bayonet.hocon;

import com.github.yingzhuo.bayonet.utility.UUIDUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * HOCON 配置文件的 {@link PropertySourceFactory} 抽象基类。
 * <p>委托 {@link PropertySourceLoader} 加载配置，处理空结果和单文档场景。
 * 多文档配置文件暂不支持。</p>
 *
 * @author 应卓
 */
public abstract class AbstractPropertySourceFactory implements PropertySourceFactory {

    private final PropertySourceLoader loader;

    /**
     * 构造器
     *
     * @param loader {@link PropertySourceLoader}，用于加载配置源
     */
    public AbstractPropertySourceFactory(PropertySourceLoader loader) {
        this.loader = loader;
    }

    @Override
    public final PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        var propertySourceName = resolvePropertySourceName(name, resource);
        var list = loader.load(propertySourceName, resource.getResource());

        if (list.isEmpty()) {
            return new EmptyPropertySource(propertySourceName);
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new UnsupportedOperationException("multiple documents is NOT supported yet.");
        }
    }

    private String resolvePropertySourceName(@Nullable String name, EncodedResource resource) {
        if (name == null) {
            name = resource.getResource().getFilename();
        }

        if (!StringUtils.hasText(name)) {
            return UUIDUtils.versionFourShort();
        }
        return name;
    }

    // ------

    /**
     * 空配置源，用于配置文件内容为空时的占位。
     */
    private static class EmptyPropertySource extends PropertySource<Object> {

        public EmptyPropertySource(String name) {
            super(name);
        }

        @Nullable
        @Override
        public Object getProperty(String name) {
            return null;
        }
    }
}
