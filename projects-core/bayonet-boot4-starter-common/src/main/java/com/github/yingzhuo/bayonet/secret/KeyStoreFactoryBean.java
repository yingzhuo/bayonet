package com.github.yingzhuo.bayonet.secret;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.security.KeyStore;

@Setter
public class KeyStoreFactoryBean implements FactoryBean<KeyStore>, ResourceLoaderAware {

    private KeyStoreType keyStoreType = KeyStoreType.PKCS12;
    private String location;
    private String storepass;
    private ResourceLoader resourceLoader;

    @Override
    public KeyStore getObject() throws Exception {
        Assert.notNull(keyStoreType, "keyStoreType must not be null");
        Assert.hasText(location, "location must not be empty");
        Assert.hasText(storepass, "storepass must not be empty");
        Assert.notNull(resourceLoader, "resourceLoader must not be null");

        try (var stream = resourceLoader.getResource(location).getInputStream()) {
            var ks = KeyStore.getInstance(keyStoreType.name());
            ks.load(stream, storepass.toCharArray());
            return ks;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return KeyStore.class;
    }

}
