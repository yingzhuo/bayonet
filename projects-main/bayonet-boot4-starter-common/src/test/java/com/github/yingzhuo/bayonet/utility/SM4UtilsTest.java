package com.github.yingzhuo.bayonet.utility;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("deprecation") // ECB 方法已弃用，此处仅为测试其功能
class SM4UtilsTest {

    private static final byte[] DATA = "hello sm4".getBytes(StandardCharsets.UTF_8);
    private static final String PLAINTEXT = "hello sm4";

    @BeforeAll
    static void beforeAll() {
        Security.addProvider(new BouncyCastleProvider());
    }

    // ============== generateKey / generateIv ==============

    @Test
    void should_generateKey_16Bytes() {
        assertThat(SM4Utils.generateKey()).hasSize(16);
    }

    @Test
    void should_generateKeyHex_andBase64_roundTrip() {
        var key = SM4Utils.generateKey();
        var keyHex = HexUtils.encodeToString(key);
        var keyBase64 = Base64.getEncoder().encodeToString(key);
        assertThat(SM4Utils.generateKeyHex()).hasSize(32);
        assertThat(HexUtils.decodeToBytes(keyHex)).isEqualTo(Base64.getDecoder().decode(keyBase64));
    }

    @Test
    void should_generateIv_16Bytes() {
        assertThat(SM4Utils.generateIv()).hasSize(16);
    }

    @Test
    void should_generateIvHex_andBase64() {
        var iv = SM4Utils.generateIv();
        assertThat(HexUtils.encodeToString(iv)).hasSize(32);
        assertThat(SM4Utils.generateIvBase64()).isNotNull();
    }

    // ============== ECB ==============

    @Test
    void should_ecb_roundTrip() {
        var key = SM4Utils.generateKey();
        var encrypted = SM4Utils.encryptEcb(key, DATA);
        assertThat(SM4Utils.decryptEcb(key, encrypted)).isEqualTo(DATA);
    }

    @Test
    void should_ecbHex_roundTrip() {
        var keyHex = SM4Utils.generateKeyHex();
        var cipherHex = SM4Utils.encryptEcbHex(keyHex, PLAINTEXT);
        assertThat(SM4Utils.decryptEcbHex(keyHex, cipherHex)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_ecbBase64_roundTrip() {
        var keyBase64 = SM4Utils.generateKeyBase64();
        var cipher = SM4Utils.encryptEcbBase64(keyBase64, PLAINTEXT);
        assertThat(SM4Utils.decryptEcbBase64(keyBase64, cipher)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_ecb_throw_when_keyLengthInvalid() {
        assertThatThrownBy(() -> SM4Utils.encryptEcb(new byte[8], DATA))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ecbHex_throw_when_plaintextIsNull() {
        assertThatThrownBy(() -> SM4Utils.encryptEcbHex(SM4Utils.generateKeyHex(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== CBC ==============

    @Test
    void should_cbc_roundTrip() {
        var key = SM4Utils.generateKey();
        var iv = SM4Utils.generateIv();
        var encrypted = SM4Utils.encryptCbc(key, iv, DATA);
        assertThat(SM4Utils.decryptCbc(key, iv, encrypted)).isEqualTo(DATA);
    }

    @Test
    void should_cbcHex_roundTrip() {
        var keyHex = SM4Utils.generateKeyHex();
        var ivHex = SM4Utils.generateIvHex();
        var cipherHex = SM4Utils.encryptCbcHex(keyHex, ivHex, PLAINTEXT);
        assertThat(SM4Utils.decryptCbcHex(keyHex, ivHex, cipherHex)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_cbcBase64_roundTrip() {
        var keyBase64 = SM4Utils.generateKeyBase64();
        var ivBase64 = SM4Utils.generateIvBase64();
        var cipher = SM4Utils.encryptCbcBase64(keyBase64, ivBase64, PLAINTEXT);
        assertThat(SM4Utils.decryptCbcBase64(keyBase64, ivBase64, cipher)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_cbc_throw_when_ivLengthInvalid() {
        var key = SM4Utils.generateKey();
        assertThatThrownBy(() -> SM4Utils.encryptCbc(key, new byte[8], DATA))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_cbcHex_throw_when_plaintextIsNull() {
        var keyHex = SM4Utils.generateKeyHex();
        var ivHex = SM4Utils.generateIvHex();
        assertThatThrownBy(() -> SM4Utils.encryptCbcHex(keyHex, ivHex, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== GCM ==============

    @Test
    void should_gcm_roundTrip() {
        var key = SM4Utils.generateKey();
        var encrypted = SM4Utils.encryptGcm(key, DATA);
        assertThat(SM4Utils.decryptGcm(key, encrypted)).isEqualTo(DATA);
    }

    @Test
    void should_gcmHex_roundTrip() {
        var keyHex = SM4Utils.generateKeyHex();
        var cipherHex = SM4Utils.encryptGcmHex(keyHex, PLAINTEXT);
        assertThat(SM4Utils.decryptGcmHex(keyHex, cipherHex)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_gcmBase64_roundTrip() {
        var keyBase64 = SM4Utils.generateKeyBase64();
        var cipher = SM4Utils.encryptGcmBase64(keyBase64, PLAINTEXT);
        assertThat(SM4Utils.decryptGcmBase64(keyBase64, cipher)).isEqualTo(PLAINTEXT);
    }

    @Test
    void should_gcm_produceDifferentCiphertext_eachTime() {
        var key = SM4Utils.generateKey();
        var e1 = SM4Utils.encryptGcm(key, DATA);
        var e2 = SM4Utils.encryptGcm(key, DATA);
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void should_gcm_throw_when_ciphertextTampered() {
        var key = SM4Utils.generateKey();
        var encrypted = SM4Utils.encryptGcm(key, DATA);
        var tampered = encrypted.clone();
        tampered[tampered.length - 1] ^= 0x01;
        assertThatThrownBy(() -> SM4Utils.decryptGcm(key, tampered))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_gcm_throw_when_wrongKey() {
        var key1 = SM4Utils.generateKey();
        var key2 = SM4Utils.generateKey();
        var encrypted = SM4Utils.encryptGcm(key1, DATA);
        assertThatThrownBy(() -> SM4Utils.decryptGcm(key2, encrypted))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_gcm_throw_when_encryptedDataTooShort() {
        assertThatThrownBy(() -> SM4Utils.decryptGcm(SM4Utils.generateKey(), new byte[4]))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
