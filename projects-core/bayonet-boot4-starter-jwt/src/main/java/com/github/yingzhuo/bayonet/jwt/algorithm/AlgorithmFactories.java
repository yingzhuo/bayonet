package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 内部工具
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AlgorithmFactories {

    public static Algorithm createAlgorithm(AlgorithmName algorithmName, PublicKey publicKey, PrivateKey privateKey) {
        Assert.notNull(algorithmName, "algorithmName must not be null");
        Assert.notNull(publicKey, "publicKey must not be null");
        Assert.notNull(privateKey, "privateKey must not be null");

        return switch (algorithmName) {
            case RSA256 -> Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case RSA384 -> Algorithm.RSA384((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case RSA512 -> Algorithm.RSA512((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case ECDSA256 -> Algorithm.ECDSA256((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            case ECDSA384 -> Algorithm.ECDSA384((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            case ECDSA512 -> Algorithm.ECDSA512((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            case RSA256PSS -> Algorithm.RSA256PSS((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case RSA384PSS -> Algorithm.RSA384PSS((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case RSA512PSS -> Algorithm.RSA512PSS((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
        };
    }

}
