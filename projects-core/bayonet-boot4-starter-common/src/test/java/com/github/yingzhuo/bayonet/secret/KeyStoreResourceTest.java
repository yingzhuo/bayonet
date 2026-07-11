package com.github.yingzhuo.bayonet.secret;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KeyStoreResourceTest {

    private static byte[] populatedKeyStoreBytes;
    private static final String STOREPASS = "storepass";
    private static final String ALIAS_KEY = "testkey";
    private static final String ALIAS_CERT = "testcert";

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        // populated keystore with both a private-key entry and a trusted-cert entry
        var p12 = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", ALIAS_KEY,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", p12.toString(),
                "-storetype", "PKCS12",
                "-storepass", STOREPASS,
                "-dname", "CN=Test",
                "-validity", "365"
        );
        assertThat(pb.start().waitFor()).as("keytool -genkeypair").isZero();

        // Export cert and re-import as trusted certificate entry
        var certFile = tempDir.resolve("cert.cer");
        pb = new ProcessBuilder(
                "keytool", "-exportcert",
                "-alias", ALIAS_KEY,
                "-keystore", p12.toString(),
                "-storepass", STOREPASS,
                "-file", certFile.toString()
        );
        assertThat(pb.start().waitFor()).as("keytool -exportcert").isZero();

        pb = new ProcessBuilder(
                "keytool", "-importcert", "-noprompt",
                "-alias", ALIAS_CERT,
                "-keystore", p12.toString(),
                "-storepass", STOREPASS,
                "-file", certFile.toString()
        );
        assertThat(pb.start().waitFor()).as("keytool -importcert").isZero();

        try (var in = java.nio.file.Files.newInputStream(p12)) {
            populatedKeyStoreBytes = in.readAllBytes();
        }
    }

    private static KeyStoreResource newResource() {
        return new KeyStoreResource(KeyStoreType.PKCS12, new ByteArrayInputStream(populatedKeyStoreBytes), STOREPASS);
    }

    // ============== 构造函数 ==============

    @Test
    void should_create_when_allArgsValid() {
        assertThat(newResource().getSecret()).isNotNull();
    }

    @Test
    void should_throw_when_typeIsNull() {
        assertThatThrownBy(() -> new KeyStoreResource(null, new ByteArrayInputStream(populatedKeyStoreBytes), STOREPASS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_streamIsNull() {
        assertThatThrownBy(() -> new KeyStoreResource(KeyStoreType.PKCS12, null, STOREPASS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassIsNull() {
        assertThatThrownBy(() -> new KeyStoreResource(KeyStoreType.PKCS12, new ByteArrayInputStream(populatedKeyStoreBytes), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_storepassWrong() {
        assertThatThrownBy(() -> new KeyStoreResource(KeyStoreType.PKCS12, new ByteArrayInputStream(populatedKeyStoreBytes), "wrongpass"))
                .isInstanceOf(UncheckedIOException.class);
    }

    @Test
    void should_throw_when_streamEmpty() {
        assertThatThrownBy(() -> new KeyStoreResource(KeyStoreType.PKCS12, new ByteArrayInputStream(new byte[0]), STOREPASS))
                .isInstanceOf(UncheckedIOException.class);
    }

    // JDK 17+ PKCS12 中 keypass 与 storepass 绑定
    @Test
    void should_throw_when_keypassMismatch() {
        assertThatThrownBy(() -> newResource().getKey(ALIAS_KEY, "wrongpass"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getSecret / getInputStream ==============

    @Test
    void should_return_keyStore_via_getSecret() {
        assertThat(newResource().getSecret()).isInstanceOf(KeyStore.class);
    }

    @Test
    void should_throw_when_getInputStream() {
        assertThatThrownBy(() -> newResource().getInputStream())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ============== getCertificateChain ==============

    @Test
    void should_return_chain_when_aliasExists() {
        assertThat(newResource().getCertificateChain(ALIAS_KEY)).isNotEmpty();
    }

    @Test
    void should_return_emptyArray_when_aliasNotExists() {
        assertThat(newResource().getCertificateChain("nonexistent")).isEmpty();
    }

    // ============== getCertificate ==============

    @Test
    void should_return_certificate_when_aliasExists() {
        assertThat(newResource().getCertificate(ALIAS_CERT)).isNotNull();
    }

    @Test
    void should_return_null_when_certificateAliasNotExists() {
        assertThat(newResource().getCertificate("nonexistent")).isNull();
    }

    // ============== getKey ==============

    @Test
    void should_return_key_when_passwordMatches() {
        // JDK 17+ PKCS12: keypass == storepass
        var key = newResource().getKey(ALIAS_KEY, STOREPASS);
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PrivateKey.class);
    }

    @Test
    void should_throw_when_keyPasswordIsNull() {
        // null 被转成 ""，与 storepass 不匹配
        assertThatThrownBy(() -> newResource().getKey(ALIAS_KEY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_return_null_when_keyAliasNotExists() {
        assertThat((Object) newResource().getKey("nonexistent")).isNull();
    }

    // ============== getPrivateKey ==============

    @Test
    void should_return_privateKey_when_passwordMatches() {
        var key = newResource().getPrivateKey(ALIAS_KEY, STOREPASS);
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PrivateKey.class);
    }

    @Test
    void should_throw_when_privateKeyPasswordIsNull() {
        assertThatThrownBy(() -> newResource().getPrivateKey(ALIAS_KEY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_return_null_when_privateKeyAliasNotExists() {
        assertThat((Object) newResource().getPrivateKey("nonexistent")).isNull();
    }

    // ============== getPublicKey ==============

    @Test
    void should_return_publicKey_when_aliasExists() {
        var key = newResource().getPublicKey(ALIAS_KEY);
        assertThat(key).isNotNull();
        assertThat(key).isInstanceOf(PublicKey.class);
    }

    @Test
    void should_return_null_when_publicKeyAliasNotExists() {
        assertThat((Object) newResource().getPublicKey("nonexistent")).isNull();
    }

    // ============== getKeyPair ==============

    @Test
    void should_return_keyPair_when_passwordMatches() {
        var pair = newResource().getKeyPair(ALIAS_KEY, STOREPASS);
        assertThat(pair).isNotNull();
        assertThat(pair.getPublic()).isNotNull();
        assertThat(pair.getPrivate()).isNotNull();
    }

    @Test
    void should_return_null_when_keyPairAliasNotExists() {
        assertThat(newResource().getKeyPair("nonexistent", STOREPASS)).isNull();
    }

}
