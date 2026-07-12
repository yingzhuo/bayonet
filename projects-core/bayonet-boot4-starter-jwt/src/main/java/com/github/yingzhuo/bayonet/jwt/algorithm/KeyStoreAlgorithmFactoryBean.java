package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.secret.KeyStoreType;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.KeyStore;
import java.security.PrivateKey;

/**
 * 从 {@link KeyStore} 加载密钥对并创建 JWT {@link Algorithm} 的 {@link FactoryBean}。
 * <p>支持 RSA 和 ECDSA 算法系列。</p>
 *
 * <pre>{@code
 * KeyStoreAlgorithmFactoryBean factory = new KeyStoreAlgorithmFactoryBean();
 * factory.setResourceLoader(new DefaultResourceLoader());
 * factory.setStoreLocation("classpath:keys.p12");
 * factory.setStorepass("secret");
 * factory.setAlias("mykey");
 * factory.setAlgorithmName("RSA256");
 * Algorithm algorithm = factory.getObject();
 * }</pre>
 */
@Setter
public class KeyStoreAlgorithmFactoryBean extends AbstractAlgorithmFactoryBean implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private KeyStoreType keyStoreType = KeyStoreType.PKCS12;
    private String storeLocation;
    private String storepass;
    private String alias;
    private String keypass;
    private String algorithmName;

    @Override
    public @Nullable Algorithm getObject() throws Exception {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.notNull(keyStoreType, "keyStoreType must not be null");
        Assert.hasText(storeLocation, "storeLocation must not be empty");
        Assert.hasText(storepass, "storepass must not be empty");
        Assert.hasText(alias, "alias must not be empty");
        Assert.hasText(algorithmName, "algorithmName must not be empty");

        if (!StringUtils.hasText(keypass)) {
            keypass = storepass;
        }

        try (var stream = resourceLoader.getResource(storeLocation).getInputStream()) {
            var ks = KeyStore.getInstance(keyStoreType.name());
            ks.load(stream, storepass.toCharArray());

            var cert = ks.getCertificate(alias);
            Assert.notNull(cert, () -> "Certificate not found for alias: '" + alias + "'");
            var publicKey = cert.getPublicKey();
            var privateKey = (PrivateKey) ks.getKey(alias, keypass.toCharArray());

            return super.createAlgorithm(algorithmName, publicKey, privateKey);
        }
    }

}
