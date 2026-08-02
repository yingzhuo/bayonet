package com.github.yingzhuo.bayonet.hocon.context;

import com.github.yingzhuo.bayonet.context.AbstractApplicationContextInitializer;
import com.github.yingzhuo.bayonet.hocon.HoconPropertySourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 在应用上下文刷新前从默认路径加载 HOCON 配置文件的初始化器。
 * <p>按优先级依次尝试以下路径，找到第一个可读的文件即加载：</p>
 * <ol>
 *   <li>{@code file:default.conf}</li>
 *   <li>{@code file:config/default.conf}</li>
 *   <li>{@code classpath:default.conf}</li>
 *   <li>{@code classpath:config/default.conf}</li>
 * </ol>
 * <p>若 {@code spring.application.name} 已配置，还会按 {@code classpath -> classpath:config -> file -> file:config}
 * 优先级尝试加载 {@code {应用名}.conf}。</p>
 * <p>若所有路径均不可用则静默跳过（设计上配置文件是可选的）。</p>
 *
 * @author 应卓
 * @see com.github.yingzhuo.bayonet.context.PropertiesLoadingInitializer
 * @since 4.1.0
 */
@Slf4j
public class HoconLoadingInitializer extends AbstractApplicationContextInitializer implements Ordered {

    private static final List<String> DEFAULT_LOCATIONS = List.of(
            "file:default.conf",
            "file:config/default.conf",
            "classpath:default.conf",
            "classpath:config/default.conf"
    );

    private static final PropertySourceLoader LOADER = new HoconPropertySourceLoader();

    public HoconLoadingInitializer() {
        setOrder(120);
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var locationList = new ArrayList<>(DEFAULT_LOCATIONS);

        var applicationName = ctx.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.hasText(applicationName)) {
            locationList.add("classpath:" + applicationName + ".conf");
            locationList.add("classpath:config/" + applicationName + ".conf");
            locationList.add("file:" + applicationName + ".conf");
            locationList.add("file:config/" + applicationName + ".conf");
        }

        var resource = super.findFirstExistingResource(ctx, locationList);
        if (resource == null) {
            return;
        }

        var name = super.getResourceFilenameOrElse(resource, "HOCON config");

        try {
            for (var propertySource : LOADER.load(name, resource)) {
                ctx.getEnvironment().getPropertySources().addFirst(propertySource);
            }
            log.debug("loaded HOCON config from: {}", name);
        } catch (Exception e) {
            log.warn("failed to load HOCON config from {}: {}", name, e.getMessage());
        }
    }
}
