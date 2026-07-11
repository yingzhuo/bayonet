package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * 数字签名工具类。
 * <p>提供便捷的签名与验签方法。</p>
 *
 * <pre>{@code
 * // 签名
 * byte[] signature = SignatureUtils.sign(data, "SHA256withRSA", privateKey);
 *
 * // 验签
 * boolean ok = SignatureUtils.verify(data, signature, "SHA256withRSA", publicKey);
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatureUtils {

    /**
     * 对数据进行签名。
     *
     * @param data       待签名的数据
     * @param sigAlgName 签名算法名称（如 {@code SHA256withRSA}）
     * @param privateKey 私钥
     * @return 签名结果字节数组
     * @throws IllegalArgumentException 若参数为 {@code null} 或签名操作失败
     */
    public static byte[] sign(byte[] data, String sigAlgName, PrivateKey privateKey) {
        Assert.notNull(data, "data must not be null");
        Assert.notNull(sigAlgName, "sigAlgName must not be null");
        Assert.notNull(privateKey, "privateKey must not be null");

        try {
            var signature = Signature.getInstance(sigAlgName);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to sign data: " + e.getMessage(), e);
        }
    }

    /**
     * 验签（使用公钥）。
     *
     * @param data       原始数据
     * @param sign       待验证的签名
     * @param sigAlgName 签名算法名称（如 {@code SHA256withRSA}）
     * @param publicKey  公钥
     * @return 签名是否有效
     * @throws IllegalArgumentException 若参数为 {@code null} 或验签操作失败
     */
    public static boolean verify(byte[] data, byte[] sign, String sigAlgName, PublicKey publicKey) {
        Assert.notNull(data, "data must not be null");
        Assert.notNull(sign, "sign must not be null");
        Assert.notNull(sigAlgName, "sigAlgName must not be null");
        Assert.notNull(publicKey, "publicKey must not be null");

        try {
            var signature = Signature.getInstance(sigAlgName);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to verify signature: " + e.getMessage(), e);
        }
    }

    /**
     * 验签（使用 {@link X509Certificate}）。
     * <p>从证书中提取签名算法名称和公钥进行验签。</p>
     *
     * @param data        原始数据
     * @param sign        待验证的签名
     * @param certificate X.509 证书
     * @return 签名是否有效
     * @throws IllegalArgumentException 若参数为 {@code null}、证书不含签名算法或验签操作失败
     */
    public static boolean verify(byte[] data, byte[] sign, X509Certificate certificate) {
        Assert.notNull(data, "data must not be null");
        Assert.notNull(sign, "sign must not be null");
        Assert.notNull(certificate, "certificate must not be null");

        var sigAlg = certificate.getSigAlgName();
        Assert.notNull(sigAlg, "Certificate has no signature algorithm");
        return verify(data, sign, sigAlg, certificate.getPublicKey());
    }

}
