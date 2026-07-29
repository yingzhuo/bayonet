package com.github.yingzhuo.bayonet.context;

import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * {@link ApplicationContextInitializer} 抽象基类
 *
 * @author 应卓
 * @since 4.1.0
 */
public abstract class AbstractApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    protected final @Nullable Resource findFirstExistingResource(ResourceLoader resourceLoader, Collection<String> locations) {
        if (resourceLoader == null || CollectionUtils.isEmpty(locations)) {
            return null;
        }

        return locations.stream()
                .filter(StringUtils::hasText)
                .map(resourceLoader::getResource)
                .filter(resource -> resource.exists() && resource.isReadable())
                .findFirst()
                .orElse(null);
    }

    protected final String getResourceFilenameOrElse(Resource resource, String defaultFilename) {
        try {
            var path = resource.getFile().toPath();
            return path.toAbsolutePath().toString();
        } catch (Exception e) {
            return defaultFilename;
        }
    }
}
