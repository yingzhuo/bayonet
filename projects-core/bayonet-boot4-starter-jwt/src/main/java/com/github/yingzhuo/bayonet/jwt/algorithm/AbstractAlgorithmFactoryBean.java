package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.FactoryBean;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * JWT {@link Algorithm} 工厂 Bean 的抽象基类。
 * <p>提供 {@link FactoryBean<Algorithm>} 的通用实现和 {@link Algorithm} 创建方法。
 * 子类需提供密钥对的来源（如 KeyStore、PEM 文件等）。</p>
 *
 * <p>支持的算法：</p>
 * <ul>
 *   <li>{@code RSA256} / {@code RSA384} / {@code RSA512} — RSA 系列</li>
 *   <li>{@code ECDSA256} / {@code ECDSA384} / {@code ECDSA512} — ECDSA 系列</li>
 * </ul>
 */
public abstract class AbstractAlgorithmFactoryBean implements FactoryBean<Algorithm> {

    @Override
    public final Class<?> getObjectType() {
        return Algorithm.class;
    }

    /**
     * 根据算法名称和密钥对创建 JWT {@link Algorithm} 实例。
     * <p>密钥类型必须与算法匹配：RSA 算法需要 {@link RSAPublicKey}/{@link RSAPrivateKey}，
     * ECDSA 算法需要 {@link ECPublicKey}/{@link ECPrivateKey}。</p>
     *
     * @param algorithmName 算法名称（{@code RSA256}、{@code ECDSA384} 等）
     * @param publicKey     公钥
     * @param privateKey    私钥
     * @return Algorithm 实例
     * @throws IllegalArgumentException 若算法名称不支持
     */
    protected final Algorithm createAlgorithm(String algorithmName, PublicKey publicKey, PrivateKey privateKey) {
        return switch (algorithmName) {
            case "RSA256" -> Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case "RSA384" -> Algorithm.RSA384((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case "RSA512" -> Algorithm.RSA512((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
            case "ECDSA256" -> Algorithm.ECDSA256((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            case "ECDSA384" -> Algorithm.ECDSA384((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            case "ECDSA512" -> Algorithm.ECDSA512((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
            default -> throw new IllegalArgumentException("Unsupported algorithm name: '" + algorithmName + "'");
        };
    }

}
