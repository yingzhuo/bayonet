package com.github.yingzhuo.bayonet.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Bouncy Castle 相关常量。
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BouncyCastleConstants {

    /**
     * 常规 Bouncy Castle Provider 名称。
     */
    public static final String BC_PROVIDER_NAME = "BC";

    /**
     * 常规 Bouncy Castle Provider 类名。
     */
    public static final String BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    /**
     * Bouncy Castle FIPS Provider 名称。
     */
    public static final String BC_FIPS_PROVIDER_NAME = "BCFIPS";

    /**
     * Bouncy Castle FIPS Provider 类名。
     */
    public static final String BC_FIPS_CLASS_NAME = "org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider";

}
