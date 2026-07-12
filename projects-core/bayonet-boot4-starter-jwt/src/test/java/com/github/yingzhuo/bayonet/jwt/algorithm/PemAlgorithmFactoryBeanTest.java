package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PemAlgorithmFactoryBeanTest {

    private static final String STOREPASS = "storepass";
    private static final String ALIAS = "testkey";
    private static byte[] combinedPemBytes;

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        // 生成 PKCS12 keystore
        var p12 = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", ALIAS,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", p12.toString(),
                "-storetype", "PKCS12",
                "-storepass", STOREPASS,
                "-dname", "CN=Test",
                "-validity", "365"
        );
        assertThat(pb.start().waitFor()).as("keytool -genkeypair").isZero();

        // 提取证书和私钥 → PEM 字节数组
        var ks = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(p12)) {
            ks.load(in, STOREPASS.toCharArray());
        }
        var cert = ks.getCertificate(ALIAS);
        var key = ks.getKey(ALIAS, STOREPASS.toCharArray());

        var sb = new StringBuilder();
        sb.append("-----BEGIN CERTIFICATE-----\n");
        sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(cert.getEncoded()));
        sb.append("\n-----END CERTIFICATE-----\n");
        sb.append("-----BEGIN PRIVATE KEY-----\n");
        sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(key.getEncoded()));
        sb.append("\n-----END PRIVATE KEY-----\n");
        combinedPemBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static PemAlgorithmFactoryBean createValidBean(ResourceLoader loader) {
        var bean = new PemAlgorithmFactoryBean();
        bean.setResourceLoader(loader);
        bean.setAlgorithmName("RSA256");
        return bean;
    }

    // ============== 成功创建 ==============

    @Test
    void should_create_RSA256_algorithm() throws Exception {
        var loader = new DefaultResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(combinedPemBytes);
            }
        };
        var bean = createValidBean(loader);
        bean.setPemLocation("pem:test");
        assertThat(bean.getObject()).isNotNull();
    }

    @Test
    void should_create_RSA384_algorithm() throws Exception {
        var loader = new DefaultResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(combinedPemBytes);
            }
        };
        var bean = createValidBean(loader);
        bean.setAlgorithmName("RSA384");
        bean.setPemLocation("pem:test");
        assertThat(bean.getObject()).isNotNull();
    }

    @Test
    void should_create_RSA512_algorithm() throws Exception {
        var loader = new DefaultResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(combinedPemBytes);
            }
        };
        var bean = createValidBean(loader);
        bean.setAlgorithmName("RSA512");
        bean.setPemLocation("pem:test");
        assertThat(bean.getObject()).isNotNull();
    }

    @Test
    void should_return_algorithm_type() {
        assertThat(createValidBean(new DefaultResourceLoader()).getObjectType()).isSameAs(Algorithm.class);
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_resourceLoaderIsNull() {
        var bean = createValidBean(null);
        bean.setResourceLoader(null);
        bean.setPemLocation("classpath:nonexistent.pem");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_pemLocationIsNull() {
        var bean = createValidBean(new DefaultResourceLoader());
        bean.setPemLocation(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_pemLocationIsEmpty() {
        var bean = createValidBean(new DefaultResourceLoader());
        bean.setPemLocation("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_algorithmNameIsNull() {
        var bean = createValidBean(new DefaultResourceLoader());
        bean.setPemLocation("classpath:nonexistent.pem");
        bean.setAlgorithmName(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_algorithmNameIsEmpty() {
        var bean = createValidBean(new DefaultResourceLoader());
        bean.setPemLocation("classpath:nonexistent.pem");
        bean.setAlgorithmName("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== PEM 内容问题 ==============

    @Test
    void should_throw_when_pemHasNoCertificates() {
        // 仅含私钥的 PEM
        var keyOnlyPem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(new byte[32]) +
                "\n-----END PRIVATE KEY-----\n";
        var loader = new DefaultResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(keyOnlyPem.getBytes(StandardCharsets.UTF_8));
            }
        };
        var bean = createValidBean(loader);
        bean.setPemLocation("pem:test");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_throw_when_pemLocationNotExists() {
        var bean = createValidBean(new DefaultResourceLoader());
        bean.setPemLocation("classpath:nonexistent.pem");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(Exception.class);
    }

    // ============== 不支持的算法 ==============

    @Test
    void should_throw_when_algorithmUnsupported() {
        var loader = new DefaultResourceLoader() {
            @Override
            public org.springframework.core.io.Resource getResource(String location) {
                return new ByteArrayResource(combinedPemBytes);
            }
        };
        var bean = createValidBean(loader);
        bean.setAlgorithmName("HMAC256");
        bean.setPemLocation("pem:test");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
