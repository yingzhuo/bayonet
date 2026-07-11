package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignatureUtilsTest {

    private static final String SIG_ALG = "SHA256withRSA";
    private static final String STOREPASS = "storepass";
    private static final byte[] DATA = "hello".getBytes(StandardCharsets.UTF_8);
    private static KeyPair keyPair;

    private static X509Certificate certificate;
    private static PrivateKey certificatePrivateKey;

    @BeforeAll
    static void setUp(@TempDir Path tempDir) throws Exception {
        // 用于常规签名/验签测试的密钥对
        var gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        keyPair = gen.generateKeyPair();

        // 用于 X509Certificate 验签测试的自签名证书及对应私钥
        var p12 = tempDir.resolve("test.p12");
        var pb = new ProcessBuilder(
                "keytool", "-genkeypair",
                "-alias", "test",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-keystore", p12.toString(),
                "-storetype", "PKCS12",
                "-storepass", STOREPASS,
                "-dname", "CN=Test",
                "-validity", "365"
        );
        assertThat(pb.start().waitFor()).as("keytool -genkeypair").isZero();

        var ks = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(p12)) {
            ks.load(in, STOREPASS.toCharArray());
        }
        certificate = (X509Certificate) ks.getCertificate("test");
        certificatePrivateKey = (PrivateKey) ks.getKey("test", STOREPASS.toCharArray());
    }

    // ============== sign ==============

    @Test
    void should_sign_when_allArgsValid() {
        var signature = SignatureUtils.sign(DATA, SIG_ALG, keyPair.getPrivate());
        assertThat(signature).isNotEmpty();
    }

    @Test
    void should_throw_when_sign_dataIsNull() {
        assertThatThrownBy(() -> SignatureUtils.sign(null, SIG_ALG, keyPair.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_sign_sigAlgIsNull() {
        assertThatThrownBy(() -> SignatureUtils.sign(DATA, null, keyPair.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_sign_privateKeyIsNull() {
        assertThatThrownBy(() -> SignatureUtils.sign(DATA, SIG_ALG, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_sign_algorithmInvalid() {
        assertThatThrownBy(() -> SignatureUtils.sign(DATA, "invalid", keyPair.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== verify (with PublicKey) ==============

    @Test
    void should_verify_true_when_signatureCorrect() {
        var signature = SignatureUtils.sign(DATA, SIG_ALG, keyPair.getPrivate());
        assertThat(SignatureUtils.verify(DATA, signature, SIG_ALG, keyPair.getPublic())).isTrue();
    }

    @Test
    void should_verify_false_when_signatureWrong() {
        var signature = SignatureUtils.sign(DATA, SIG_ALG, keyPair.getPrivate());
        signature[0] ^= 0xFF;
        assertThat(SignatureUtils.verify(DATA, signature, SIG_ALG, keyPair.getPublic())).isFalse();
    }

    @Test
    void should_throw_when_verify_dataIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(null, new byte[1], SIG_ALG, keyPair.getPublic()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verify_signIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(DATA, null, SIG_ALG, keyPair.getPublic()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verify_sigAlgIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(DATA, new byte[1], null, keyPair.getPublic()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verify_publicKeyIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(DATA, new byte[1], SIG_ALG, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== verify (with X509Certificate) ==============

    @Test
    void should_verifyWithCert_true_when_signatureCorrect() {
        var sigAlg = certificate.getSigAlgName();
        var signature = SignatureUtils.sign(DATA, sigAlg, certificatePrivateKey);
        assertThat(SignatureUtils.verify(DATA, signature, certificate)).isTrue();
    }

    @Test
    void should_throw_when_verifyWithCert_dataIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(null, new byte[1], certificate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verifyWithCert_signIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(DATA, null, certificate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verifyWithCert_certIsNull() {
        assertThatThrownBy(() -> SignatureUtils.verify(DATA, new byte[1], (X509Certificate) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
