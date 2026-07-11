package com.github.yingzhuo.bayonet.secret;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyStoreProtocolResolver implements ProtocolResolver {

    private static final Pattern LOCATION = Pattern.compile(
            "^keystore:(?<location>[^?]+)(?:\\?(?<params>.*))?$"
    );

    private static final Pattern PARAM = Pattern.compile("(?<key>\\w+)=(?<value>[^&]+)");

    @Override
    @Nullable
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        var m = LOCATION.matcher(location);
        if (!m.matches()) {
            return null;
        }

        var innerLocation = m.group("location");
        var queryString = m.group("params");

        var type = KeyStoreType.PKCS12;
        String storepass = null;

        if (queryString != null) {
            var pm = PARAM.matcher(queryString);
            while (pm.find()) {
                var key = pm.group("key");
                var value = URLDecoder.decode(pm.group("value").replace("+", "%2B"), UTF_8);
                if ("type".equals(key)) {
                    type = KeyStoreType.valueOf(value.toUpperCase());
                } else if ("storepass".equals(key)) {
                    storepass = value;
                }
            }
        }

        Assert.notNull(storepass, () ->
                "storepass must not be null; ensure the keystore: URL includes ?storepass=<password>");

        var innerResource = resourceLoader.getResource(innerLocation);
        try (InputStream is = innerResource.getInputStream()) {
            return new KeyStoreResource(type, is, storepass);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load keystore resource: " + innerLocation, e);
        }
    }

}
