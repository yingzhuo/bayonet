package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PemResourceTest {

    private static final String STOREPASS = "storepass";
    private static final String ALIAS = "testkey";
    private static byte[] combinedPemBytes;       // cert + key
    private static byte[] certOnlyPemBytes;        // cert only
    private static byte[] keyOnlyPemBytes;          // key only

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        // Generate a PKCS12 keystore via keytool
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

        // Load keystore and extract entries
        var ks = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(p12)) {
            ks.load(in, STOREPASS.toCharArray());
        }

        var cert = ks.getCertificate(ALIAS);
        var key = ks.getKey(ALIAS, STOREPASS.toCharArray());

        // combined PEM (cert + key)
        combinedPemBytes = toPemBytes(cert.getEncoded(), key.getEncoded());

        // cert-only PEM
        certOnlyPemBytes = toPemBytes(cert.getEncoded(), null);

        // key-only PEM
        keyOnlyPemBytes = toPemBytes(null, key.getEncoded());
    }

    private static byte[] toPemBytes(byte[] certDer, byte[] keyDer) {
        var sb = new StringBuilder();
        if (certDer != null) {
            sb.append("-----BEGIN CERTIFICATE-----\n");
            sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(certDer));
            sb.append("\n-----END CERTIFICATE-----\n");
        }
        if (keyDer != null) {
            sb.append("-----BEGIN PRIVATE KEY-----\n");
            sb.append(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyDer));
            sb.append("\n-----END PRIVATE KEY-----\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static PemResource newCombinedResource() {
        return new PemResource(new ByteArrayInputStream(combinedPemBytes));
    }

    // ============== 构造函数 ==============

    @Test
    void should_create_when_validPem() {
        var r = newCombinedResource();
        assertThat(r.getSecret()).isNotNull();
    }

    @Test
    void should_throw_when_streamIsNull() {
        // PemContent.load(null) 抛出异常，构造函数未做 Assert.notNull
        assertThatThrownBy(() -> new PemResource(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void should_create_when_streamEmpty() {
        // PemContent.load 对空流也能构造成功，只是无内容
        var r = new PemResource(new ByteArrayInputStream(new byte[0]));
        assertThat(r.getCertificates()).isEmpty();
    }

    @Test
    void should_create_when_invalidContent() {
        // PemContent.load 对无效文本也能构造成功，只是无内容
        var invalid = new ByteArrayInputStream("not a pem file".getBytes(StandardCharsets.UTF_8));
        var r = new PemResource(invalid);
        assertThat(r.getCertificates()).isEmpty();
    }

    // ============== getSecret / getInputStream ==============

    @Test
    void should_return_pemContent_via_getSecret() {
        assertThat(newCombinedResource().getSecret()).isNotNull();
    }

    @Test
    void should_throw_when_getInputStream() {
        assertThatThrownBy(() -> newCombinedResource().getInputStream())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== getCertificates ==============

    @Test
    void should_return_certificates_when_pemHasCerts() {
        assertThat(newCombinedResource().getCertificates()).isNotEmpty();
    }

    @Test
    void should_return_notEmptyList_when_certOnlyPem() {
        var r = new PemResource(new ByteArrayInputStream(certOnlyPemBytes));
        assertThat(r.getCertificates()).isNotEmpty();
    }

    @Test
    void should_return_emptyList_when_noCertsInPem() {
        var r = new PemResource(new ByteArrayInputStream(keyOnlyPemBytes));
        assertThat(r.getCertificates()).isEmpty();
    }

    // ============== getCertificate ==============

    @Test
    void should_return_firstCertificate_when_pemHasCerts() {
        var cert = newCombinedResource().getCertificate();
        assertThat(cert).isNotNull();
        assertThat(cert).isInstanceOf(X509Certificate.class);
    }

    @Test
    void should_return_null_when_noCertsInPem() {
        var r = new PemResource(new ByteArrayInputStream(keyOnlyPemBytes));
        assertThat(r.getCertificate()).isNull();
    }

    // ============== getPrivateKey ==============

    @Test
    void should_return_privateKey_when_noPassword() {
        var key = newCombinedResource().getPrivateKey(null);
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PrivateKey.class);
    }

    @Test
    void should_return_privateKey_when_passwordProvided() {
        // unencrypted key — password is ignored
        var key = newCombinedResource().getPrivateKey("whatever");
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PrivateKey.class);
    }

    @Test
    void should_return_null_when_noKeyInPem() {
        // Spring Boot 4.x PemContent 无私钥时返回 null
        var r = new PemResource(new ByteArrayInputStream(certOnlyPemBytes));
        assertThat((Object) r.getPrivateKey(null)).isNull();
    }

    // ============== getPublicKey ==============

    @Test
    void should_return_publicKey_when_pemHasCerts() {
        var key = newCombinedResource().getPublicKey();
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PublicKey.class);
    }

    @Test
    void should_return_null_when_publicKeyMissingInPem() {
        var r = new PemResource(new ByteArrayInputStream(keyOnlyPemBytes));
        assertThat((Object) r.getPublicKey()).isNull();
    }

    // ============== getKeyPair ==============

    @Test
    void should_return_keyPair_when_bothAvailable() {
        var pair = newCombinedResource().getKeyPair(null);
        assertThat(pair).isNotNull();
        assertThat(pair.getPublic()).isNotNull();
        assertThat(pair.getPrivate()).isNotNull();
    }

    @Test
    void should_return_null_when_privateKeyMissing() {
        var r = new PemResource(new ByteArrayInputStream(certOnlyPemBytes));
        assertThat(r.getKeyPair(null)).isNull();
    }

    @Test
    void should_return_null_when_publicKeyMissing() {
        var r = new PemResource(new ByteArrayInputStream(keyOnlyPemBytes));
        assertThat(r.getKeyPair(null)).isNull();
    }

}
