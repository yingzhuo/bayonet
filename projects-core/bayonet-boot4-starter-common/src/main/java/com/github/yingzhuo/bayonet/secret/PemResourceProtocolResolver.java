package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.regex.Pattern;

/**
 * Spring {@link ProtocolResolver}，解析 {@code pem:} 协议的资源位置。
 * <p>格式：{@code pem:<资源路径>}</p>
 *
 * <pre>{@code
 * pem:classpath:server.pem
 * pem:file:/etc/ssl/cert.pem
 * }</pre>
 */
public class PemResourceProtocolResolver implements ProtocolResolver {

    private static final Pattern LOCATION = Pattern.compile(
            "^pem:(?<location>[^?]+)(?:\\?(?<params>.*))?$"
    );

    @Override
    @Nullable
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        var m = LOCATION.matcher(location);
        if (!m.matches()) {
            return null;
        }

        var innerLocation = m.group("location");

        var innerResource = resourceLoader.getResource(innerLocation);
        try (InputStream is = innerResource.getInputStream()) {
            return new PemResource(is);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load pem resource: " + innerLocation, e);
        }
    }

}
