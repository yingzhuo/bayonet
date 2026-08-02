package com.github.yingzhuo.bayonet.hocon.configdata;

import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.core.io.Resource;

/**
 * HOCON（{@code .conf}）配置文件的 {@link ConfigDataResource} 实现。
 * <p>封装一个 HOCON 配置文件资源，由 {@link HoconConfigDataLocationResolver} 生成，
 * 由 {@link HoconConfigDataLoader} 加载。</p>
 *
 * @author 应卓
 * @see HoconConfigDataLocationResolver
 * @see HoconConfigDataLoader
 * @since 4.1.1
 */
public class HoconConfigDataResource extends ConfigDataResource {

    private final Resource resource;

    /**
     * 构造器。
     *
     * @param resource HOCON 配置文件资源（非 {@code null}）
     */
    public HoconConfigDataResource(Resource resource) {
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
        if (!(obj instanceof HoconConfigDataResource that)) {
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
        return "HoconConfigDataResource{resource=" + resource + '}';
    }
}
