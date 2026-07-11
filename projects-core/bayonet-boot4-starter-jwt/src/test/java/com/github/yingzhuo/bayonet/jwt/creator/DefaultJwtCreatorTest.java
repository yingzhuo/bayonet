package com.github.yingzhuo.bayonet.jwt.creator;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultJwtCreatorTest {

    private static Algorithm algorithm;

    @BeforeAll
    static void setUp() throws Exception {
        var gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        var keyPair = gen.generateKeyPair();
        algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
    }

    // ============== 构造器 ==============

    @Test
    void should_create_when_algorithmValid() {
        assertThat(new DefaultJwtCreator(algorithm)).isNotNull();
    }

    @Test
    void should_throw_when_algorithmIsNull() {
        assertThatThrownBy(() -> new DefaultJwtCreator(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== createToken ==============

    @Test
    void should_create_when_dataValid() {
        var data = JwtData.newInstance()
                .addPayloadSubject("test-user")
                .addPayloadIssuer("bayonet");

        var token = new DefaultJwtCreator(algorithm).create(data);
        assertThat(token).isNotBlank();
    }

    @Test
    void should_create_withCustomHeaders() {
        var data = JwtData.newInstance()
                .addHeaderKeyId("key-1")
                .addPayloadSubject("test");

        var token = new DefaultJwtCreator(algorithm).create(data);
        assertThat(token).isNotBlank();
    }

    @Test
    void should_throw_when_dataIsNull() {
        var creator = new DefaultJwtCreator(algorithm);
        assertThatThrownBy(() -> creator.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
