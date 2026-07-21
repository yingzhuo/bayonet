package com.github.yingzhuo.bayonet.webcli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.altindag.ssl.SSLFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SSLFactoryFactories {

    public static SSLFactory createUnsafe() {
        // @formatter:off
        return SSLFactory.builder()
                .withUnsafeTrustMaterial()
                .withUnsafeHostnameVerifier()
                .build();
        // @formatter:on
    }

    public static SSLFactory createDefault() {
        // @formatter:off
        return SSLFactory.builder()
                .withDefaultTrustMaterial()
                .build();
        // @formatter:on
    }

}
