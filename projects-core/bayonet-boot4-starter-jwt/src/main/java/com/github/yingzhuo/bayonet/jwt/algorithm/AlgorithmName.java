package com.github.yingzhuo.bayonet.jwt.algorithm;

/**
 * JWT 非对称算法名称枚举。
 * <p>涵盖 RSA 签名系列、RSA-PSS 签名系列及 ECDSA 签名系列。</p>
 *
 * <pre>{@code
 * RSA256      — RSA with SHA-256
 * RSA384      — RSA with SHA-384
 * RSA512      — RSA with SHA-512
 * RSA256PSS   — RSA-PSS with SHA-256
 * RSA384PSS   — RSA-PSS with SHA-384
 * RSA512PSS   — RSA-PSS with SHA-512
 * ECDSA256    — ECDSA with P-256
 * ECDSA384    — ECDSA with P-384
 * ECDSA512    — ECDSA with P-521
 * }</pre>
 */
public enum AlgorithmName {

    /**
     * RSA with SHA-256
     */
    RSA256,

    /**
     * RSA with SHA-384
     */
    RSA384,

    /**
     * RSA with SHA-512
     */
    RSA512,

    /**
     * RSA-PSS with SHA-256
     */
    RSA256PSS,

    /**
     * RSA-PSS with SHA-384
     */
    RSA384PSS,

    /**
     * RSA-PSS with SHA-512
     */
    RSA512PSS,

    /**
     * ECDSA with P-256
     */
    ECDSA256,

    /**
     * ECDSA with P-384
     */
    ECDSA384,

    /**
     * ECDSA with P-521
     */
    ECDSA512

}
