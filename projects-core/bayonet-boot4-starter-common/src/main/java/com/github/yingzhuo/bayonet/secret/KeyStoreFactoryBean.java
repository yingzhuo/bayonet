package com.github.yingzhuo.bayonet.secret;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.security.KeyStore;

/**
 * Spring {@link FactoryBean}，用于创建 {@link KeyStore} 实例。
 * <p>通过 {@code location} 指定密钥库资源位置，
 * {@code storepass} 指定密钥库密码。</p>
 *
 * <pre>{@code
 * @Bean
 * public KeyStoreFactoryBean keyStore() {
 *     var fb = new KeyStoreFactoryBean();
 *     fb.setLocation("classpath:keystore.p12");
 *     fb.setStorepass("changeit");
 *     return fb;
 * }
 * }</pre>
 */
@Setter
public class KeyStoreFactoryBean implements FactoryBean<KeyStore>, ResourceLoaderAware {

    /**
     * 密钥库类型，默认 {@link KeyStoreType#PKCS12}
     */
    private KeyStoreType keyStoreType = KeyStoreType.PKCS12;

    /** 密钥库资源位置（如 {@code classpath:keystore.p12}） */
    private String location;

    /** 密钥库密码 */
    private String storepass;

    /** Spring 资源加载器，由容器自动注入 */
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
