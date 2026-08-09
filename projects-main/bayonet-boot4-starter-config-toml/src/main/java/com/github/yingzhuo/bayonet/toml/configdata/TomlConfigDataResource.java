package com.github.yingzhuo.bayonet.toml.configdata;

import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;

/**
 * TOML（{@code .toml}）配置文件的 {@link ConfigDataResource} 实现。
 * <p>封装一个 TOML 配置文件资源，由 {@link TomlConfigDataLocationResolver} 生成，
 * 由 {@link TomlConfigDataLoader} 加载。</p>
 *
 * @author 应卓
 * @see TomlConfigDataLocationResolver
 * @see TomlConfigDataLoader
 * @since 4.1.1
 */
public class TomlConfigDataResource extends ConfigDataResource {

    private final Resource resource;

    /**
     * 构造器。
     *
     * @param resource TOML 配置文件资源（非 {@code null}）
     */
    public TomlConfigDataResource(Resource resource) {
        super(false);
        this.resource = resource;
    }

    /**
     * 获取封装的配置文件资源。
     *
     * @return 资源（非 {@code null}）
     */
    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TomlConfigDataResource that)) {
            return false;
        }
        return resource.equals(that.resource);
    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("resource", resource)
                .toString();
    }
}
