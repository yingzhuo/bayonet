package com.github.yingzhuo.bayonet.secret;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.security.KeyStore;

/**
 * Spring {@link FactoryBean}，用于从 KeyStore 文件中加载 {@link SecretKey}。
 *
 * <p>通过 {@link ResourceLoaderAware} 支持 classpath:/、file:/ 等资源位置。
 * 本 Bean 默认以单例方式工作，{@link #getObject()} 通常仅调用一次。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class SecretKeyFactoryBean implements FactoryBean<SecretKey>, ResourceLoaderAware {

    // @formatter:off
    private ResourceLoader resourceLoader;
    private @Setter @Nullable KeyStoreType storeType = KeyStoreType.getDefault();
    private @Setter String storeLocation;
    private @Setter String storepass;
    private @Setter String alias;
    private @Setter @Nullable String keypass;
    // @formatter:on

    /**
     * 从 KeyStore 中获取 {@link SecretKey}。
     *
     * @return {@link SecretKey}（可能为 {@code null}，当别名不存在时）
     * @throws Exception 加载或获取密钥失败时抛出
     */
    @Override
    public SecretKey getObject() throws Exception {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.notNull(storeType, "storeType is required");
        Assert.hasText(storeLocation, "secret key store location must not be empty");
        Assert.hasText(storepass, "secret key store password must not be empty");
        Assert.hasText(alias, "secret key alias must not be empty");

        var storepassChars = storepass.toCharArray();
        var keypassChars = keypass != null ? keypass.toCharArray() : storepassChars;

        try (var stream = resourceLoader.getResource(storeLocation).getInputStream()) {
            var keyStore = KeyStore.getInstance(storeType.getName());
            keyStore.load(stream, storepassChars);

            var key = keyStore.getKey(alias, keypassChars);

            if (key instanceof SecretKey secretKey) {
                return secretKey;
            }
            throw new IllegalStateException("Secret key not found for alias: " + alias);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return SecretKey.class;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
