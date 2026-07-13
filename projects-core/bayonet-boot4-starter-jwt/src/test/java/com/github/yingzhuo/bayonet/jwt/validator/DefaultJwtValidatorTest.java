package com.github.yingzhuo.bayonet.jwt.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.yingzhuo.bayonet.jwt.blacklist.BlacklistChecker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultJwtValidatorTest {

    private static final String SUBJECT = "test-user";
    private static Algorithm algorithm;
    private static String validToken;

    @BeforeAll
    static void setUp() throws Exception {
        var gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        var keyPair = gen.generateKeyPair();

        algorithm = Algorithm.RSA256(
                (RSAPublicKey) keyPair.getPublic(),
                (RSAPrivateKey) keyPair.getPrivate()
        );

        validToken = JWT.create()
                .withSubject(SUBJECT)
                .withIssuer("bayonet")
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .sign(algorithm);
    }

    // ============== 构造器 ==============

    @Test
    void should_create_when_algorithmValid() {
        assertThat(new DefaultJwtValidator(algorithm)).isNotNull();
    }

    @Test
    void should_create_when_algorithmValid_withCustomizer() {
        var validator = new DefaultJwtValidator(algorithm, v -> v.acceptLeeway(3));
        assertThat(validator).isNotNull();
    }

    @Test
    void should_create_when_customizerIsNull() {
        var validator = new DefaultJwtValidator(algorithm, null);
        assertThat(validator).isNotNull();
    }

    @Test
    void should_create_when_blacklistCheckerIsNull() {
        var validator = new DefaultJwtValidator(algorithm, null, null);
        assertThat(validator).isNotNull();
    }

    @Test
    void should_throw_when_algorithmIsNull() {
        assertThatThrownBy(() -> new DefaultJwtValidator(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== validateToken — 参数校验 ==============

    @Test
    void should_throw_when_tokenIsNull() {
        var validator = new DefaultJwtValidator(algorithm);
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_tokenIsNull_withCustomizer() {
        var validator = new DefaultJwtValidator(algorithm, v -> v.acceptLeeway(3));
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== validateToken — 验证结果 ==============

    @Test
    void should_return_OK_when_tokenValid() {
        var result = new DefaultJwtValidator(algorithm).validate(validToken);
        assertThat(result).isEqualTo(ValidatingResult.OK);
    }

    @Test
    void should_return_OK_when_tokenValid_withCustomizer() {
        var validator = new DefaultJwtValidator(algorithm, v -> v.acceptLeeway(3));
        assertThat(validator.validate(validToken)).isEqualTo(ValidatingResult.OK);
    }

    @Test
    void should_return_INVALID_TIME_when_tokenExpired() {
        var gen = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(Date.from(Instant.now().minusSeconds(10)))
                .sign(algorithm);

        var result = new DefaultJwtValidator(algorithm).validate(gen);
        assertThat(result).isEqualTo(ValidatingResult.INVALID_TIME);
    }

    @Test
    void should_return_INVALID_SIGNATURE_when_wrongKey() throws Exception {
        var gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        var otherPair = gen.generateKeyPair();
        var otherAlg = Algorithm.RSA256(
                (RSAPublicKey) otherPair.getPublic(),
                (RSAPrivateKey) otherPair.getPrivate()
        );

        var token = JWT.create()
                .withSubject(SUBJECT)
                .sign(otherAlg);

        var result = new DefaultJwtValidator(algorithm).validate(token);
        assertThat(result).isEqualTo(ValidatingResult.INVALID_SIGNATURE);
    }

    @Test
    void should_return_INVALID_JWT_FORMAT_when_garbage() {
        var result = new DefaultJwtValidator(algorithm).validate("not.a.jwt");
        assertThat(result).isEqualTo(ValidatingResult.INVALID_JWT_FORMAT);
    }

    @Test
    void should_return_INVALID_CLAIM_when_missingRequiredClaim() {
        var validator = new DefaultJwtValidator(algorithm, v -> v.withClaimPresence("nonexistent"));
        var result = validator.validate(validToken);
        assertThat(result).isEqualTo(ValidatingResult.INVALID_CLAIM);
    }

    @Test
    void should_return_INVALID_TIME_when_notBeforeFuture() {
        var token = JWT.create()
                .withSubject(SUBJECT)
                .withNotBefore(Date.from(Instant.now().plusSeconds(3600)))
                .sign(algorithm);

        var result = new DefaultJwtValidator(algorithm).validate(token);
        assertThat(result).isEqualTo(ValidatingResult.INVALID_TIME);
    }

    // ============== BlacklistChecker ==============

    @Test
    void should_return_INVALID_BLACKLISTED_when_checkerRejects() {
        var checker = (BlacklistChecker) (raw, decoded) -> true;
        var validator = new DefaultJwtValidator(algorithm, null, checker);

        var result = validator.validate(validToken);
        assertThat(result).isEqualTo(ValidatingResult.INVALID_BLACKLISTED);
    }

    @Test
    void should_return_OK_when_checkerAccepts() {
        var checker = (BlacklistChecker) (raw, decoded) -> false;
        var validator = new DefaultJwtValidator(algorithm, null, checker);

        var result = validator.validate(validToken);
        assertThat(result).isEqualTo(ValidatingResult.OK);
    }

    @Test
    void should_return_INVALID_JWT_FORMAT_when_garbageWithChecker() {
        var checker = (BlacklistChecker) (raw, decoded) -> true;
        var validator = new DefaultJwtValidator(algorithm, null, checker);

        var result = validator.validate("garbage.token.here");
        assertThat(result).isEqualTo(ValidatingResult.INVALID_JWT_FORMAT);
    }

}
