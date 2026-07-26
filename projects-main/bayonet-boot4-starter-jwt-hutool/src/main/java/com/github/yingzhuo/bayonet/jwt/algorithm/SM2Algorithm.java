package com.github.yingzhuo.bayonet.jwt.algorithm;

import cn.hutool.crypto.asymmetric.SM2;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 基于国密 SM2 算法的 JWT 签名算法实现。
 *
 * <p>使用 Hutool 的 {@link SM2} 进行签名和验签，支持通过 Hex 编码密钥、字节数组或 Java 原生
 * {@link PublicKey}/{@link PrivateKey} 构造。SM2 是一种基于椭圆曲线密码学的非对称算法，
 * 适用于 JWT 的 RSxxx 类算法的国密替代方案。</p>
 *
 * <p>签名算法名称为 {@code "SM2"}。</p>
 *
 * @author 应卓
 * @see SM2
 * @since 4.1.1
 */
public class SM2Algorithm extends Algorithm {

    private static final String NAME = "SM2";
    private static final String DESCRIPTION = "SM3withSM2";

    private final SM2 sm2;

    /**
     * 使用 Hex 编码的密钥对构造实例。
     *
     * @param publicKeyBase64Encoded  Base64 编码的公钥（非空）
     * @param privateKeyBase64Encoded Base64 编码的私钥（非空）
     */
    public SM2Algorithm(String publicKeyBase64Encoded, String privateKeyBase64Encoded) {
        super(NAME, DESCRIPTION);
        Assert.hasText(publicKeyBase64Encoded, "public key must not be empty");
        Assert.hasText(privateKeyBase64Encoded, "private key must not be empty");
        this.sm2 = new SM2(privateKeyBase64Encoded, publicKeyBase64Encoded);
    }

    /**
     * 使用字节数组形式的密钥对构造实例。
     *
     * @param publicKey  公钥字节数组（非 {@code null}）
     * @param privateKey 私钥字节数组（非 {@code null}）
     */
    public SM2Algorithm(byte[] publicKey, byte[] privateKey) {
        super(NAME, DESCRIPTION);
        Assert.notNull(publicKey, "public key must not be null");
        Assert.notNull(privateKey, "private key must not be null");
        this.sm2 = new SM2(privateKey, publicKey);
    }

    /**
     * 使用 Java 原生 {@link PublicKey} 和 {@link PrivateKey} 构造实例。
     *
     * @param publicKey  公钥（非 {@code null}）
     * @param privateKey 私钥（非 {@code null}）
     */
    public SM2Algorithm(PublicKey publicKey, PrivateKey privateKey) {
        super(NAME, DESCRIPTION);
        Assert.notNull(publicKey, "public key must not be null");
        Assert.notNull(privateKey, "private key must not be null");
        this.sm2 = new SM2(privateKey, publicKey);
    }

    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        try {
            var contentBytes = (jwt.getHeader() + "." + jwt.getPayload()).getBytes(StandardCharsets.UTF_8);
            var signatureBytes = Base64.getUrlDecoder().decode(jwt.getSignature());

            if (!sm2.verify(contentBytes, signatureBytes)) {
                throw new SignatureVerificationException(this);
            }
        } catch (Exception e) {
            throw new SignatureVerificationException(this, e);
        }
    }

    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        try {
            return sm2.sign(contentBytes);
        } catch (Exception e) {
            throw new SignatureGenerationException(this, e);
        }
    }
}
