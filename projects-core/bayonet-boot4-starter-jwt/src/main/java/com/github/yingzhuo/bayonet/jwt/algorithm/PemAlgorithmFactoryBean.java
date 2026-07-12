package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * 从 PEM 文件加载证书和私钥并创建 JWT {@link Algorithm} 的 {@link FactoryBean}。
 * <p>支持 RSA 和 ECDSA 算法系列。PEM 文件中必须包含证书。</p>
 *
 * <pre>{@code
 * PemAlgorithmFactoryBean factory = new PemAlgorithmFactoryBean();
 * factory.setResourceLoader(new DefaultResourceLoader());
 * factory.setPemLocation("classpath:keys.pem");
 * factory.setAlgorithmName("RSA256");
 * Algorithm algorithm = factory.getObject();
 * }</pre>
 */
@Setter
public class PemAlgorithmFactoryBean extends AbstractAlgorithmFactoryBean implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private String pemLocation;
    private @Nullable String keypass;
    private String algorithmName;

    @Override
    public Algorithm getObject() throws Exception {
        Assert.notNull(resourceLoader, "resourceLoader must not be null");
        Assert.hasText(pemLocation, "pemLocation must not be empty");
        Assert.hasText(algorithmName, "algorithmName must not be empty");

        try (var stream = resourceLoader.getResource(pemLocation).getInputStream()) {
            var pemContent = PemContent.load(stream);

            var certs = pemContent.getCertificates();
            Assert.notEmpty(certs, "PEM file contains no certificates");
            var publicKey = certs.get(0).getPublicKey();
            Assert.notNull(publicKey, "Certificate contains no public key");

            var privateKey = pemContent.getPrivateKey(keypass);
            Assert.notNull(privateKey, "privateKey must not be null");

            return super.createAlgorithm(algorithmName, publicKey, privateKey);
        }
    }

}
