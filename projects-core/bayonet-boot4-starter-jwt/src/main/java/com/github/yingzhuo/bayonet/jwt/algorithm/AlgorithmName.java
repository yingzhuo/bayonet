package com.github.yingzhuo.bayonet.jwt.algorithm;

/**
 * JWT 算法名称枚举。
 *
 * <pre>{@code
 * RSA256    — RSA with SHA-256
 * RSA384    — RSA with SHA-384
 * RSA512    — RSA with SHA-512
 * ECDSA256  — ECDSA with P-256
 * ECDSA384  — ECDSA with P-384
 * ECDSA512  — ECDSA with P-512
 * }</pre>
 */
public enum AlgorithmName {

    RSA256,
    RSA384,
    RSA512,
    ECDSA256,
    ECDSA384,
    ECDSA512

}
