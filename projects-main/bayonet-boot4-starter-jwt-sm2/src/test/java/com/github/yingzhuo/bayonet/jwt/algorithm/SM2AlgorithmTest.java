package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.JWT;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SM2AlgorithmTest {

    private static KeyPair keyPair;

    @BeforeAll
    static void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        var generator = KeyPairGenerator.getInstance("EC", "BC");
        generator.initialize(new ECGenParameterSpec("sm2p256v1"));
        keyPair = generator.generateKeyPair();
    }

    private static byte[] privateKeyBytes() {
        var d = ((BCECPrivateKey) keyPair.getPrivate()).getD();
        return BigIntegers.asUnsignedByteArray(32, d);
    }

    private static byte[] publicKeyBytes() {
        return ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false);
    }

    private static void assertSignAndVerify(SM2Algorithm algorithm) {
        var token = JWT.create().withSubject("bayonet").sign(algorithm);
        assertThatCode(() -> algorithm.verify(JWT.decode(token))).doesNotThrowAnyException();
    }

    @Test
    void should_sign_and_verify_when_constructed_with_byte_array_keys() {
        assertSignAndVerify(new SM2Algorithm(publicKeyBytes(), privateKeyBytes()));
    }

    @Test
    void should_sign_and_verify_when_constructed_with_hex_string_keys() {
        assertSignAndVerify(new SM2Algorithm(Hex.toHexString(publicKeyBytes()), Hex.toHexString(privateKeyBytes())));
    }

    @Test
    void should_sign_and_verify_when_constructed_with_base64_string_keys() {
        assertSignAndVerify(new SM2Algorithm(
                Base64.getEncoder().encodeToString(publicKeyBytes()),
                Base64.getEncoder().encodeToString(privateKeyBytes())
        ));
    }

    @Test
    void should_sign_and_verify_when_constructed_with_java_security_keys() {
        assertSignAndVerify(new SM2Algorithm(keyPair.getPublic(), keyPair.getPrivate()));
    }

    @Test
    void should_produce_signature_verifiable_by_standard_sm3withsm2() throws Exception {
        var algorithm = new SM2Algorithm(keyPair.getPublic(), keyPair.getPrivate());
        var content = "header.payload".getBytes(StandardCharsets.UTF_8);

        var mySignature = algorithm.sign(content);

        var standard = Signature.getInstance("SM3withSM2", "BC");
        standard.initVerify(keyPair.getPublic());
        standard.update(content);
        assertThat(standard.verify(mySignature)).isTrue();
    }

    @Test
    void should_verify_signature_from_standard_sm3withsm2() throws Exception {
        var algorithm = new SM2Algorithm(keyPair.getPublic(), keyPair.getPrivate());
        var header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"SM2\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        var payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"bayonet\"}".getBytes(StandardCharsets.UTF_8));
        var content = (header + "." + payload).getBytes(StandardCharsets.UTF_8);

        var standard = Signature.getInstance("SM3withSM2", "BC");
        standard.initSign(keyPair.getPrivate());
        standard.update(content);
        var standardSignature = standard.sign();

        var token = header + "." + payload + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(standardSignature);
        assertThatCode(() -> algorithm.verify(JWT.decode(token))).doesNotThrowAnyException();
    }
}
