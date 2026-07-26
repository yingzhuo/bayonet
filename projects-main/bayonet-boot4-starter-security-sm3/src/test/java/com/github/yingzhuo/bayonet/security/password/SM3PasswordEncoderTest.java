package com.github.yingzhuo.bayonet.security.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SM3PasswordEncoderTest {

    private SM3PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new SM3PasswordEncoder();
    }

    @Test
    void should_return_hash_when_input_is_not_null() {
        var hash = encoder.encode("hello");
        assertThat(hash).isNotNull().isNotEmpty();
    }

    @Test
    void should_return_null_when_input_is_null() {
        assertThat(encoder.encode(null)).isNull();
    }

    @Test
    void should_be_deterministic() {
        var hash1 = encoder.encode("password123");
        var hash2 = encoder.encode("password123");
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void should_return_true_when_password_matches() {
        var hash = encoder.encode("secret");
        assertThat(encoder.matches("secret", hash)).isTrue();
    }

    @Test
    void should_return_false_when_password_does_not_match() {
        var hash = encoder.encode("secret");
        assertThat(encoder.matches("wrong", hash)).isFalse();
    }

    @Test
    void should_return_false_when_raw_password_is_null() {
        var hash = encoder.encode("secret");
        assertThat(encoder.matches(null, hash)).isFalse();
    }

    @Test
    void should_return_false_when_encoded_password_is_null() {
        assertThat(encoder.matches("secret", null)).isFalse();
    }

    @Test
    void should_return_false_when_raw_password_is_empty() {
        var hash = encoder.encode("secret");
        assertThat(encoder.matches("", hash)).isFalse();
    }

    @Test
    void should_return_false_when_encoded_password_is_empty() {
        assertThat(encoder.matches("secret", "")).isFalse();
    }
}
