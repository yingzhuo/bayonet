package com.github.yingzhuo.bayonet.context;

import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public abstract class AbstractApplicationContextInitializer<T extends ConfigurableApplicationContext> implements ApplicationContextInitializer<T> {

    protected final @Nullable Resource loadResource(ResourceLoader resourceLoader, String location) {
        var resource = resourceLoader.getResource(location);
        return resource.exists() && resource.isReadable() ? resource : null;
    }

}
