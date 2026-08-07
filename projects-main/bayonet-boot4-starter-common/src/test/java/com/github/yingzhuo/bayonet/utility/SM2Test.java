package com.github.yingzhuo.bayonet.utility;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SM2Test {

    private static final byte[] CONTENT = "hello sm2".getBytes(StandardCharsets.UTF_8);

    @BeforeAll
    static void beforeAll() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
    }

    // ============== PublicKey / PrivateKey 构造 ==============

    @Test
    void should_signAndVerify_withPublicPrivateKey() throws Exception {
        var keyPair = generateKeyPair();
        var sm2 = new SM2(keyPair.getPublic(), keyPair.getPrivate());
        var signature = sm2.sign(CONTENT);
        assertThat(signature).isNotEmpty();
        assertThat(sm2.verify(CONTENT, signature)).isTrue();
    }

    @Test
    void should_verifyFalse_whenContentTampered() throws Exception {
        var keyPair = generateKeyPair();
        var sm2 = new SM2(keyPair.getPublic(), keyPair.getPrivate());
        var signature = sm2.sign(CONTENT);

        var tampered = CONTENT.clone();
        tampered[tampered.length - 1] ^= 0x01;
        assertThat(sm2.verify(tampered, signature)).isFalse();
    }

    @Test
    void should_verifyFalse_whenWrongKey() throws Exception {
        var keyPair1 = generateKeyPair();
        var keyPair2 = generateKeyPair();
        var sm2 = new SM2(keyPair1.getPublic(), keyPair1.getPrivate());
        var signature = sm2.sign(CONTENT);

        var wrong = new SM2(keyPair2.getPublic(), keyPair2.getPrivate());
        assertThat(wrong.verify(CONTENT, signature)).isFalse();
    }

    // ============== byte[] 构造 ==============

    @Test
    void should_signAndVerify_withByteArray() throws Exception {
        var keyPair = generateKeyPair();
        var publicKeyBytes = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false); // 04||X||Y
        var privateKeyBytes = ((BCECPrivateKey) keyPair.getPrivate()).getD().toByteArray();

        var sm2 = new SM2(publicKeyBytes, privateKeyBytes);
        var signature = sm2.sign(CONTENT);
        assertThat(sm2.verify(CONTENT, signature)).isTrue();
    }

    // ============== String 构造 ==============

    @Test
    void should_signAndVerify_withHexString() throws Exception {
        var keyPair = generateKeyPair();
        var publicKeyText = HexUtils.encodeToString(((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false));
        var privateKeyText = HexUtils.encodeToString(((BCECPrivateKey) keyPair.getPrivate()).getD().toByteArray());

        var sm2 = new SM2(publicKeyText, privateKeyText);
        var signature = sm2.sign(CONTENT);
        assertThat(sm2.verify(CONTENT, signature)).isTrue();
    }

    @Test
    void should_signAndVerify_withBase64String() throws Exception {
        var keyPair = generateKeyPair();
        var publicKeyText = Base64.getEncoder().encodeToString(((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false));
        var privateKeyText = Base64.getEncoder().encodeToString(((BCECPrivateKey) keyPair.getPrivate()).getD().toByteArray());

        var sm2 = new SM2(publicKeyText, privateKeyText);
        var signature = sm2.sign(CONTENT);
        assertThat(sm2.verify(CONTENT, signature)).isTrue();
    }

    // ============== 参数校验 ==============

    @Test
    void should_throw_when_publicKeyTextBlank() {
        assertThatThrownBy(() -> new SM2("", "abc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_privateKeyTextBlank() {
        assertThatThrownBy(() -> new SM2("abc", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_byteArrayIsNull() {
        assertThatThrownBy(() -> new SM2((byte[]) null, new byte[32]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_signContentIsNull() throws Exception {
        var keyPair = generateKeyPair();
        var sm2 = new SM2(keyPair.getPublic(), keyPair.getPrivate());
        assertThatThrownBy(() -> sm2.sign(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_verifyContentIsNull() throws Exception {
        var keyPair = generateKeyPair();
        var sm2 = new SM2(keyPair.getPublic(), keyPair.getPrivate());
        assertThatThrownBy(() -> sm2.verify(null, new byte[32]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ------

    private static KeyPair generateKeyPair() throws Exception {
        var generator = KeyPairGenerator.getInstance("EC", "BC");
        generator.initialize(new ECGenParameterSpec("sm2p256v1"));
        return generator.generateKeyPair();
    }

}
