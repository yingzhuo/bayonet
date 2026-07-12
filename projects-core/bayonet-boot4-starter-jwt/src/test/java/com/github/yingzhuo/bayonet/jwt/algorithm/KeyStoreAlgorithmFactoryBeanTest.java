package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyStoreAlgorithmFactoryBeanTest {

    private static final String STOREPASS = "storepass";
    private static final String ALIAS = "testkey";
    private static final String KEYPASS = "";
    private static Path keystorePath;

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        keystorePath = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", ALIAS,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", keystorePath.toString(),
                "-storetype", "PKCS12",
                "-storepass", STOREPASS,
                "-dname", "CN=Test",
                "-validity", "365"
        );
        assertThat(pb.start().waitFor()).as("keytool -genkeypair").isZero();
    }

    private static KeyStoreAlgorithmFactoryBean createValidBean(String algorithmName) {
        var bean = new KeyStoreAlgorithmFactoryBean();
        bean.setResourceLoader(new DefaultResourceLoader());
        bean.setStoreLocation("file:" + keystorePath);
        bean.setStorepass(STOREPASS);
        bean.setAlias(ALIAS);
        bean.setKeypass(KEYPASS);
        bean.setAlgorithmName(algorithmName);
        return bean;
    }

    // ============== 成功创建 ==============

    @Test
    void should_create_RSA256_algorithm() throws Exception {
        assertThat(createValidBean("RSA256").getObject()).isNotNull();
    }

    @Test
    void should_create_RSA384_algorithm() throws Exception {
        assertThat(createValidBean("RSA384").getObject()).isNotNull();
    }

    @Test
    void should_create_RSA512_algorithm() throws Exception {
        assertThat(createValidBean("RSA512").getObject()).isNotNull();
    }

    @Test
    void should_return_algorithm_type() {
        assertThat(createValidBean("RSA256").getObjectType()).isSameAs(Algorithm.class);
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_resourceLoaderIsNull() {
        var bean = createValidBean("RSA256");
        bean.setResourceLoader(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storeLocationIsNull() {
        var bean = createValidBean("RSA256");
        bean.setStoreLocation(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storeLocationIsEmpty() {
        var bean = createValidBean("RSA256");
        bean.setStoreLocation("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassIsNull() {
        var bean = createValidBean("RSA256");
        bean.setStorepass(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassIsEmpty() {
        var bean = createValidBean("RSA256");
        bean.setStorepass("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_aliasIsNull() {
        var bean = createValidBean("RSA256");
        bean.setAlias(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_aliasIsEmpty() {
        var bean = createValidBean("RSA256");
        bean.setAlias("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_algorithmNameIsNull() {
        var bean = createValidBean("RSA256");
        bean.setAlgorithmName(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_algorithmNameIsEmpty() {
        var bean = createValidBean("RSA256");
        bean.setAlgorithmName("");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_keyStoreTypeIsNull() {
        var bean = createValidBean("RSA256");
        bean.setKeyStoreType(null);
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 不存在的 alias ==============

    @Test
    void should_throw_when_aliasNotExists() {
        var bean = createValidBean("RSA256");
        bean.setAlias("nonexistent");
        assertThatThrownBy(() -> bean.getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 不支持的算法 ==============

    @Test
    void should_throw_when_algorithmUnsupported() {
        assertThatThrownBy(() -> createValidBean("HMAC256").getObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
