package com.github.yingzhuo.bayonet.hocon.configdata;

import org.apache.commons.logging.Log;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.config.*;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持 {@code hocon:} 前缀配置位置的 {@link ConfigDataLocationResolver}。
 * <p>用于 {@code spring.config.import=hocon:...} 或 {@code spring.config.additional-location=hocon:...}。
 * 支持两种形式：</p>
 * <ul>
 *   <li>目录（以 {@code /} 结尾）：自动发现 {@code application.conf} 及各 profile 变体
 *       {@code application-{profile}.conf}</li>
 *   <li>具体文件：直接加载该文件</li>
 * </ul>
 *
 * <pre>{@code
 * spring.config.import=hocon:classpath:/config/          # 目录
 * spring.config.import=hocon:file:./app.conf            # 具体文件
 * spring.config.import=optional:hocon:file:./extra.conf # 可选，文件不存在时不报错
 * }</pre>
 *
 * <p>{@code optional:} 前缀由 {@link ConfigDataLocation} 剥离并记录，本类通过
 * {@link ConfigDataLocation#isOptional()} 判断。</p>
 *
 * @author 应卓
 * @see HoconConfigDataLoader
 * @since 4.1.1
 */
public class HoconConfigDataLocationResolver implements ConfigDataLocationResolver<HoconConfigDataResource>, Ordered {

    private static final String PREFIX = "hocon:";
    private static final String DEFAULT_CONFIG_NAME = "application";

    private final Log logger;
    private final ResourceLoader resourceLoader;

    /**
     * 构造器。
     *
     * @param logFactory     延迟日志工厂（由 Spring Boot 自动注入）
     * @param resourceLoader 资源加载器（由 Spring Boot 自动注入）
     */
    public HoconConfigDataLocationResolver(DeferredLogFactory logFactory, ResourceLoader resourceLoader) {
        this.logger = logFactory.getLog(HoconConfigDataLocationResolver.class);
        this.resourceLoader = resourceLoader;
    }

    private static boolean isDirectory(String resourceLocation) {
        return resourceLocation.endsWith("/");
    }

    private static String profileSuffix(@Nullable String profile) {
        return profile != null ? "-" + profile : "";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.hasPrefix(PREFIX);
    }

    @Override
    public List<HoconConfigDataResource> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location)
            throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        return resolve(location, null);
    }

    @Override
    public List<HoconConfigDataResource> resolveProfileSpecific(ConfigDataLocationResolverContext context,
                                                                ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        var result = new ArrayList<HoconConfigDataResource>();
        for (var profile : profiles) {
            result.addAll(resolve(location, profile));
        }
        return result;
    }

    private List<HoconConfigDataResource> resolve(ConfigDataLocation location, @Nullable String profile) {
        var resourceLocation = location.getNonPrefixedValue(PREFIX);
        var fullLocation = isDirectory(resourceLocation)
                ? resourceLocation + DEFAULT_CONFIG_NAME + profileSuffix(profile) + ".conf"
                : resourceLocation;

        var resource = resourceLoader.getResource(fullLocation);
        if (!resource.exists()) {
            if (location.isOptional()) {
                return List.of();
            }
            throw new ConfigDataResourceNotFoundException(new HoconConfigDataResource(resource));
        }
        logger.debug("resolved HOCON config data location: " + fullLocation);
        return List.of(new HoconConfigDataResource(resource));
    }
}
