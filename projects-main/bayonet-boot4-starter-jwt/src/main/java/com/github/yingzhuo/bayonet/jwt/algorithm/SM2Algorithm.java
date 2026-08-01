package com.github.yingzhuo.bayonet.jwt.algorithm;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 基于国密 SM2 算法的 JWT 签名算法实现。
 *
 * <p>基于 BouncyCastle 轻量级 API（{@link SM2Signer}）直接实现签名与验签，不依赖任何第三方封装库。
 * 支持通过 Hex 编码密钥、字节数组或 Java 原生 {@link PublicKey}/{@link PrivateKey} 构造。
 * SM2 是一种基于椭圆曲线密码学的非对称算法，适用于 JWT 的 RSA 等算法的国密替代方案。</p>
 *
 * <p>签名算法名称为 {@code "SM2"}，签名输出为 DER 编码，与标准 {@code SM3withSM2} 完全兼容。</p>
 *
 * @author 应卓
 * @see SM2Signer
 * @since 4.1.1
 */
public class SM2Algorithm extends Algorithm {

    private static final String NAME = "SM2";
    private static final String DESCRIPTION = "SM3withSM2";

    private static final X9ECParameters X9EC_PARAMS = GMNamedCurves.getByName("sm2p256v1");
    private static final ECDomainParameters DOMAIN_PARAMS =
            new ECDomainParameters(X9EC_PARAMS.getCurve(), X9EC_PARAMS.getG(), X9EC_PARAMS.getN(), X9EC_PARAMS.getH());
    private static final ECCurve CURVE = X9EC_PARAMS.getCurve();

    private final ECPrivateKeyParameters privateKeyParams;
    private final ECPublicKeyParameters publicKeyParams;

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
        this.privateKeyParams = decodePrivateKey(decodeText(privateKeyText));
        this.publicKeyParams = decodePublicKey(decodeText(publicKeyText));
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
        this.privateKeyParams = decodePrivateKey(privateKey);
        this.publicKeyParams = decodePublicKey(publicKey);
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
        try {
            this.privateKeyParams = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(privateKey);
            this.publicKeyParams = (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(publicKey);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("invalid SM2 key pair", e);
        }
    }

    /**
     * 解码 Hex 或 Base64 编码的密钥字节。
     *
     * @param text 密钥文本（非空）
     * @return 解码后的字节数组
     */
    private static byte[] decodeText(String text) {
        return isHex(text) ? Hex.decode(text) : Base64.getDecoder().decode(text);
    }

    private static boolean isHex(String text) {
        for (int i = 0; i < text.length(); i++) {
            var c = text.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析私钥字节为 {@link ECPrivateKeyParameters}，字节为大端序的 D 值。
     *
     * @param bytes 私钥字节数组
     * @return {@link ECPrivateKeyParameters}（非 {@code null}）
     */
    private static ECPrivateKeyParameters decodePrivateKey(byte[] bytes) {
        return new ECPrivateKeyParameters(new BigInteger(1, bytes), DOMAIN_PARAMS);
    }

    /**
     * 解析公钥字节为 {@link ECPublicKeyParameters}，支持未压缩点（{@code 04||X||Y}）或 X.509 编码。
     *
     * @param bytes 公钥字节数组
     * @return {@link ECPublicKeyParameters}（非 {@code null}）
     * @throws IllegalArgumentException 当公钥字节无法解析时
     */
    private static ECPublicKeyParameters decodePublicKey(byte[] bytes) {
        try {
            return new ECPublicKeyParameters(CURVE.decodePoint(bytes), DOMAIN_PARAMS);
        } catch (Exception ignored) {
            // 尝试 X.509 / PKCS#1
        }
        try {
            var spki = SubjectPublicKeyInfo.getInstance(bytes);
            return new ECPublicKeyParameters(CURVE.decodePoint(spki.getPublicKeyData().getOctets()), DOMAIN_PARAMS);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid SM2 public key", e);
        }
    }

    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        try {
            var contentBytes = (jwt.getHeader() + "." + jwt.getPayload()).getBytes(StandardCharsets.UTF_8);
            var signatureBytes = Base64.getUrlDecoder().decode(jwt.getSignature());

            var signer = new SM2Signer();
            signer.init(false, publicKeyParams);
            signer.update(contentBytes, 0, contentBytes.length);
            if (!signer.verifySignature(signatureBytes)) {
                throw new SignatureVerificationException(this);
            }
        } catch (Exception e) {
            throw new SignatureVerificationException(this, e);
        }
    }

    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        try {
            var signer = new SM2Signer();
            signer.init(true, new ParametersWithRandom(privateKeyParams));
            signer.update(contentBytes, 0, contentBytes.length);
            return signer.generateSignature();
        } catch (Exception e) {
            throw new SignatureGenerationException(this, e);
        }
    }
}
