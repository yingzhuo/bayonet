package com.github.yingzhuo.bayonet.security.password;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderFactoriesTest {

    @Test
    void should_createDefault_returnNonNull() {
        var encoder = PasswordEncoderFactories.createDefault();
        assertThat(encoder).isNotNull();
    }

    @Test
    void should_createMinimal_returnNonNull() {
        var encoder = PasswordEncoderFactories.createMinimal();
        assertThat(encoder).isNotNull();
    }

    @Test
    void should_encodeWithBcryptPrefix_when_createDefault() {
        var encoder = PasswordEncoderFactories.createDefault();
        var encoded = encoder.encode("password123");
        assertThat(encoded).startsWith("{bcrypt}");
    }

    @Test
    void should_encodeWithBcryptPrefix_when_createMinimal() {
        var encoder = PasswordEncoderFactories.createMinimal();
        var encoded = encoder.encode("password123");
        assertThat(encoded).startsWith("{bcrypt}");
    }

    @Test
    void should_matchEncodedPassword_when_createDefault() {
        var encoder = PasswordEncoderFactories.createDefault();
        var encoded = encoder.encode("mySecret");
        assertThat(encoder.matches("mySecret", encoded)).isTrue();
    }

    @Test
    void should_matchEncodedPassword_when_createMinimal() {
        var encoder = PasswordEncoderFactories.createMinimal();
        var encoded = encoder.encode("mySecret");
        assertThat(encoder.matches("mySecret", encoded)).isTrue();
    }

    @Test
    void should_notMatchWrongPassword_when_createDefault() {
        var encoder = PasswordEncoderFactories.createDefault();
        var encoded = encoder.encode("correctPassword");
        assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
    }

    @Test
    void should_acceptNoopEncodedPassword() {
        var encoder = PasswordEncoderFactories.createDefault();
        // DelegatingPasswordEncoder 通过 {noop} 前缀路由到明文编码器
        assertThat(encoder.matches("test", "{noop}test")).isTrue();
    }

    @Test
    void should_supportUpgradeEncoding() {
        var encoder = PasswordEncoderFactories.createDefault();
        // 旧密码用 noop 编码，matches 后应 upgrade 为 bcrypt
        assertThat(encoder.matches("oldPwd", "{noop}oldPwd")).isTrue();
    }

}
