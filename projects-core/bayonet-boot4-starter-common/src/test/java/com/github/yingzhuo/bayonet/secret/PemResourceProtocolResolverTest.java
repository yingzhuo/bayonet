package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PemResourceProtocolResolverTest {

    private static final String STOREPASS = "storepass";
    private static final String ALIAS = "testkey";
    private static Path pemFile;
    private final PemResourceProtocolResolver resolver = new PemResourceProtocolResolver();
    private final ResourceLoader loader = new DefaultResourceLoader();

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        // Generate keystore via keytool
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

        // Load keystore and extract cert + key
        var ks = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(p12)) {
            ks.load(in, STOREPASS.toCharArray());
        }

        var cert = ks.getCertificate(ALIAS);
        var key = ks.getKey(ALIAS, STOREPASS.toCharArray());

        // Write combined PEM file
        var sb = new StringBuilder();
        sb.append("-----BEGIN CERTIFICATE-----\n");
        sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(cert.getEncoded()));
        sb.append("\n-----END CERTIFICATE-----\n");
        sb.append("-----BEGIN PRIVATE KEY-----\n");
        sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(key.getEncoded()));
        sb.append("\n-----END PRIVATE KEY-----\n");

        pemFile = tempDir.resolve("test.pem");
        try (OutputStream os = Files.newOutputStream(pemFile)) {
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    // ============== 协议匹配 ==============

    @Test
    void should_return_null_when_prefixNotMatch() {
        assertThat(resolver.resolve("classpath:test.pem", loader)).isNull();
    }

    @Test
    void should_return_null_when_emptyString() {
        assertThat(resolver.resolve("", loader)).isNull();
    }

    @Test
    void should_return_null_when_otherProtocol() {
        assertThat(resolver.resolve("other:classpath:test.pem", loader)).isNull();
    }

    // ============== 成功解析 ==============

    @Test
    void should_resolve_withFileProtocol() {
        var location = "pem:file:" + pemFile;
        var result = resolver.resolve(location, loader);
        assertThat(result).isNotNull().isInstanceOf(PemResource.class);
        assertThat(((PemResource) result).getSecret()).isNotNull();
    }

    // ============== 参数缺失/错误 ==============

    @Test
    void should_throw_when_innerResourceNotFound() {
        var location = "pem:file:/nonexistent/nope.pem";
        assertThatThrownBy(() -> resolver.resolve(location, loader))
                .isInstanceOf(Exception.class);
    }

    // ============== 集成测试：通过 ResourceLoader ==============

    @Test
    void should_resolve_when_registeredWithResourceLoader() {
        var ld = new DefaultResourceLoader();
        ld.addProtocolResolver(resolver);

        var resource = ld.getResource("pem:file:" + pemFile);
        assertThat(resource).isInstanceOf(PemResource.class);
    }

    @Test
    void should_delegateToDefault_when_prefixNotMatch() {
        var ld = new DefaultResourceLoader();
        ld.addProtocolResolver(resolver);

        var resource = ld.getResource("file:" + pemFile);
        assertThat(resource).isNotNull();
        assertThat(resource).isNotInstanceOf(PemResource.class);
    }

}
