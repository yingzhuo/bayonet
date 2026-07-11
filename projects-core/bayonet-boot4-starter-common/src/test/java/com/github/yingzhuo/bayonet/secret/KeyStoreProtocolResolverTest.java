package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyStoreProtocolResolverTest {

    private static final String STOREPASS = "storepass";
    private static Path keystorePath;
    private final KeyStoreProtocolResolver resolver = new KeyStoreProtocolResolver();
    private final ResourceLoader loader = new DefaultResourceLoader();

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        keystorePath = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", "testkey",
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

    // ============== 协议匹配 ==============

    @Test
    void should_return_null_when_prefixNotMatch() {
        assertThat(resolver.resolve("classpath:test.p12", loader)).isNull();
    }

    @Test
    void should_return_null_when_emptyString() {
        assertThat(resolver.resolve("", loader)).isNull();
    }

    @Test
    void should_return_null_when_otherProtocol() {
        assertThat(resolver.resolve("other:classpath:test.p12", loader)).isNull();
    }

    // ============== 成功解析 ==============

    @Test
    void should_resolve_withFileProtocol() {
        var location = "keystore:file:" + keystorePath + "?storepass=" + STOREPASS;
        var result = resolver.resolve(location, loader);
        assertThat(result).isNotNull().isInstanceOf(KeyStoreResource.class);
        assertThat(((KeyStoreResource) result).getSecret()).isNotNull();
    }

    @Test
    void should_resolve_withExplicitType() {
        var location = "keystore:file:" + keystorePath + "?type=PKCS12&storepass=" + STOREPASS;
        var result = resolver.resolve(location, loader);
        assertThat(result).isNotNull().isInstanceOf(KeyStoreResource.class);
    }

    @Test
    void should_resolve_withDefaultTypePkcs12() {
        var location = "keystore:file:" + keystorePath + "?storepass=" + STOREPASS;
        var result = resolver.resolve(location, loader);
        assertThat(result).isNotNull().isInstanceOf(KeyStoreResource.class);
    }

    // ============== 参数缺失/错误 ==============

    @Test
    void should_throw_when_storepassMissing() {
        var location = "keystore:file:" + keystorePath + "?type=PKCS12";
        assertThatThrownBy(() -> resolver.resolve(location, loader))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_noQueryString() {
        var location = "keystore:file:" + keystorePath;
        assertThatThrownBy(() -> resolver.resolve(location, loader))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_typeInvalid() {
        var location = "keystore:file:" + keystorePath + "?type=INVALID&storepass=" + STOREPASS;
        assertThatThrownBy(() -> resolver.resolve(location, loader))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_innerResourceNotFound() {
        var location = "keystore:file:/nonexistent/nope.p12?storepass=" + STOREPASS;
        assertThatThrownBy(() -> resolver.resolve(location, loader))
                .isInstanceOf(Exception.class);
    }

    // ============== 集成测试：通过 ResourceLoader ==============

    @Test
    void should_resolve_when_registeredWithResourceLoader() {
        var ld = new DefaultResourceLoader();
        ld.addProtocolResolver(resolver);

        var resource = ld.getResource("keystore:file:" + keystorePath + "?storepass=" + STOREPASS);
        assertThat(resource).isInstanceOf(KeyStoreResource.class);
    }

    @Test
    void should_delegateToDefault_when_prefixNotMatch() {
        var ld = new DefaultResourceLoader();
        ld.addProtocolResolver(resolver);

        var resource = ld.getResource("file:" + keystorePath);
        assertThat(resource).isNotNull();
        assertThat(resource).isNotInstanceOf(KeyStoreResource.class);
    }

}
