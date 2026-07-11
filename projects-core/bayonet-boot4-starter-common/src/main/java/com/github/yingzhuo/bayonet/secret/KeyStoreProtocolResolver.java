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
import java.nio.charset.StandardCharsets;

public class KeyStoreProtocolResolver implements ProtocolResolver {

    private static final String PREFIX = "keystore:";

    @Override
    @Nullable
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (!location.startsWith(PREFIX)) {
            return null;
        }

        var stripped = location.substring(PREFIX.length());
        var queryIndex = stripped.indexOf('?');

        var innerLocation = (queryIndex >= 0) ? stripped.substring(0, queryIndex) : stripped;
        var queryString = (queryIndex >= 0) ? stripped.substring(queryIndex + 1) : "";

        var type = KeyStoreType.PKCS12;
        String storepass = null;

        for (var param : queryString.split("&")) {
            var eqIndex = param.indexOf('=');
            if (eqIndex < 0) continue;

            var key = param.substring(0, eqIndex);
            var value = URLDecoder.decode(param.substring(eqIndex + 1), StandardCharsets.UTF_8);

            if ("type".equals(key)) {
                type = KeyStoreType.valueOf(value.toUpperCase());
            } else if ("storepass".equals(key)) {
                storepass = value;
            }
        }

        Assert.notNull(storepass, () ->
                "storepass must not be null; ensure the keystore: URL includes ?storepass=<password>");
        Assert.notNull(type, () -> "type must not be null; supported: PKCS12, JKS");

        var innerResource = resourceLoader.getResource(innerLocation);
        try (InputStream is = innerResource.getInputStream()) {
            return new KeyStoreResource(type, is, storepass);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load keystore resource: " + innerLocation, e);
        }
    }

}
