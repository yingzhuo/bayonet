package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.yingzhuo.bayonet.utility.SM2;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 基于国密 SM2 算法的 JWT 签名算法实现。
 *
 * <p>签名与验签逻辑委托给 {@link SM2}（BouncyCastle 轻量级 API），
 * 支持通过 Hex 编码密钥、字节数组或 Java 原生 {@link PublicKey}/{@link PrivateKey} 构造。
 * SM2 是一种基于椭圆曲线密码学的非对称算法，适用于 JWT 的 RSA 等算法的国密替代方案。</p>
 *
 * <p>签名算法名称为 {@code "SM2"}，签名输出为 DER 编码，与标准 {@code SM3withSM2} 完全兼容。</p>
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
     * 使用 Bas64或Hex 编码的密钥对构造实例。
     *
     * @param publicKeyText  Bas64或Hex 编码的公钥（非空）
     * @param privateKeyText Bas64或Hex 编码的私钥（非空）
     */
    public SM2Algorithm(String publicKeyText, String privateKeyText) {
        super(NAME, DESCRIPTION);
        Assert.hasText(publicKeyText, "public key text must not be empty");
        Assert.hasText(privateKeyText, "private key text must not be empty");
        this.sm2 = new SM2(publicKeyText, privateKeyText);
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
        this.sm2 = new SM2(publicKey, privateKey);
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
        this.sm2 = new SM2(publicKey, privateKey);
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
