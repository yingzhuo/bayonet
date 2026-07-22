package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RSACryptoUtilsTest {

    private static final KeyPair KEY_PAIR;

    static {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KEY_PAIR = generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    // ============== encrypt / decrypt（短数据）==============

    @Test
    void should_encryptAndDecrypt() {
        var plain = "hello rsa";
        var encrypted = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());
        var decrypted = RSACryptoUtils.decrypt(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void should_produce_differentCiphertext_eachTime() {
        var plain = "hello";
        var e1 = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());
        var e2 = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void should_handle_emptyString() {
        var plain = "";
        var encrypted = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());
        var decrypted = RSACryptoUtils.decrypt(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEmpty();
    }

    @Test
    void should_handle_chineseCharacters() {
        var plain = "你好，世界！";
        var encrypted = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());
        var decrypted = RSACryptoUtils.decrypt(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEqualTo(plain);
    }

    // ============== encryptLong / decryptLong（长数据）==============

    @Test
    void should_encryptAndDecryptLong() {
        var plain = "a".repeat(500);
        var encrypted = RSACryptoUtils.encryptLong(plain, KEY_PAIR.getPublic());
        assertThat(encrypted).contains("|");
        var decrypted = RSACryptoUtils.decryptLong(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void should_handle_shortText_withEncryptLong() {
        var plain = "hi";
        var encrypted = RSACryptoUtils.encryptLong(plain, KEY_PAIR.getPublic());
        assertThat(encrypted).doesNotContain("|");
        var decrypted = RSACryptoUtils.decryptLong(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void should_handle_longChinese_withEncryptLong() {
        var plain = "中".repeat(300);
        var encrypted = RSACryptoUtils.encryptLong(plain, KEY_PAIR.getPublic());
        assertThat(encrypted).contains("|");
        var decrypted = RSACryptoUtils.decryptLong(encrypted, KEY_PAIR.getPrivate());
        assertThat(decrypted).isEqualTo(plain);
    }

    // ============== 参数验证 ==============

    @Test
    void should_throw_when_encrypt_plainTextIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.encrypt(null, KEY_PAIR.getPublic()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_encrypt_publicKeyIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.encrypt("data", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decrypt_cipherTextIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.decrypt(null, KEY_PAIR.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decrypt_privateKeyIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.decrypt("data", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_encryptLong_plainTextIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.encryptLong(null, KEY_PAIR.getPublic()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_encryptLong_publicKeyIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.encryptLong("data", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decryptLong_cipherTextIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.decryptLong(null, KEY_PAIR.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decryptLong_privateKeyIsNull() {
        assertThatThrownBy(() -> RSACryptoUtils.decryptLong("data", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 错误密钥 ==============

    @Test
    void should_throw_when_decrypt_withWrongKey() throws Exception {
        var encrypted = RSACryptoUtils.encrypt("secret", KEY_PAIR.getPublic());
        var wrongPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        assertThatThrownBy(() -> RSACryptoUtils.decrypt(encrypted, wrongPair.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_decryptLong_withWrongKey() throws Exception {
        var encrypted = RSACryptoUtils.encryptLong("test data", KEY_PAIR.getPublic());
        var wrongPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        assertThatThrownBy(() -> RSACryptoUtils.decryptLong(encrypted, wrongPair.getPrivate()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 密钥大小动态计算 ==============

    @Test
    void should_work_with_differentKeySizes() throws Exception {
        for (int keySize : new int[]{1024, 2048}) {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySize);
            var pair = generator.generateKeyPair();

            var plain = "x".repeat(Math.min(200, keySize / 8 - 11));
            var encrypted = RSACryptoUtils.encryptLong(plain, pair.getPublic());
            var decrypted = RSACryptoUtils.decryptLong(encrypted, pair.getPrivate());
            assertThat(decrypted).isEqualTo(plain);
        }
    }

    // ============== encrypt 和 encryptLong 结果互通 ==============

    @Test
    void should_be_consistent_between_shortAndLong() {
        var plain = "hello";
        var shortEncrypted = RSACryptoUtils.encrypt(plain, KEY_PAIR.getPublic());

        assertThat(RSACryptoUtils.decryptLong(shortEncrypted, KEY_PAIR.getPrivate())).isEqualTo(plain);
        assertThat(RSACryptoUtils.encryptLong(plain, KEY_PAIR.getPublic())).isNotBlank();
    }

}
