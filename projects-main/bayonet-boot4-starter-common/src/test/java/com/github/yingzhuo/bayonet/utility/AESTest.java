package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AESTest {

    private static final byte[] DATA = "hello aes".getBytes(StandardCharsets.UTF_8);

    // ============== generateKey ==============

    @Test
    void should_generate_defaultKey() {
        var key = AES.generateKey();
        assertThat(key.getAlgorithm()).isEqualTo("AES");
        assertThat(key.getEncoded()).hasSize(32); // 256 bits
    }

    @Test
    void should_generate_key_withCustomSize() {
        var key = AES.generateKey(128);
        assertThat(key.getEncoded()).hasSize(16); // 128 bits
    }

    // ============== restoreKey ==============

    @Test
    void should_restoreKey() {
        var original = AES.generateKey();
        var restored = AES.restoreKey(original.getEncoded());
        assertThat(restored.getAlgorithm()).isEqualTo("AES");
    }

    @Test
    void should_throw_when_restoreKey_encodedIsNull() {
        assertThatThrownBy(() -> AES.restoreKey(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 构造器 ==============

    @Test
    void should_create_with_SecretKey() {
        var aes = new AES(AES.generateKey());
        assertThat(aes).isNotNull();
    }

    @Test
    void should_create_with_byteArray() {
        var aes = new AES(AES.generateKey().getEncoded());
        assertThat(aes).isNotNull();
    }

    @Test
    void should_create_with_base64String() {
        var key = AES.generateKey();
        var encoded = Base64.getUrlEncoder().encodeToString(key.getEncoded());
        var aes = new AES(encoded);
        assertThat(aes).isNotNull();
    }

    @Test
    void should_throw_when_SecretKeyIsNull() {
        assertThatThrownBy(() -> new AES((SecretKey) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_byteArrayIsNull() {
        assertThatThrownBy(() -> new AES((byte[]) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_base64StringIsBlank() {
        assertThatThrownBy(() -> new AES(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== encrypt / decrypt ==============

    @Test
    void should_encryptAndDecrypt() {
        var aes = new AES(AES.generateKey());
        var encrypted = aes.encrypt(DATA);
        var decrypted = aes.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(DATA);
    }

    @Test
    void should_produce_differentCiphertext_eachTime() {
        var aes = new AES(AES.generateKey());
        var e1 = aes.encrypt(DATA);
        var e2 = aes.encrypt(DATA);
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void should_throw_when_encrypt_dataIsNull() {
        var aes = new AES(AES.generateKey());
        assertThatThrownBy(() -> aes.encrypt(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decrypt_encryptedDataIsNull() {
        var aes = new AES(AES.generateKey());
        assertThatThrownBy(() -> aes.decrypt(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decrypt_dataTooShort() {
        var aes = new AES(AES.generateKey());
        assertThatThrownBy(() -> aes.decrypt(new byte[3]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decrypt_withWrongKey() {
        var aes1 = new AES(AES.generateKey());
        var aes2 = new AES(AES.generateKey());
        var encrypted = aes1.encrypt(DATA);
        assertThatThrownBy(() -> aes2.decrypt(encrypted))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getSecretKeyAsBase64 ==============

    @Test
    void should_exportKeyAsBase64() {
        var aes = new AES(AES.generateKey());
        var exported = aes.getSecretKeyAsBase64();
        assertThat(exported).isNotNull();
        // 可以 round-trip
        var restored = new AES(exported);
        var encrypted = aes.encrypt(DATA);
        assertThat(restored.decrypt(encrypted)).isEqualTo(DATA);
    }

    // ============== 跨构造器互通 ==============

    @Test
    void should_encryptAndDecrypt_withDifferentConstructors() {
        var key = AES.generateKey();
        var encoded = Base64.getUrlEncoder().encodeToString(key.getEncoded());

        var aes1 = new AES(key);
        var aes2 = new AES(encoded);

        var encrypted = aes1.encrypt(DATA);
        var decrypted = aes2.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(DATA);
    }

}
