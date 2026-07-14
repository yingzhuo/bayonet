package com.github.yingzhuo.bayonet.secret;

import com.github.yingzhuo.bayonet.utility.ResourceUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.pem.PemContent;
import org.springframework.util.Assert;

/**
 * {@link KeyBundle} 的工厂类。
 * <p>提供从 PEM 文件或 KeyStore 加载密钥与证书链的便捷方法。</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyBundleFactories {

    /**
     * 从 PEM 文件加载 {@link KeyBundle}。
     * <p>PEM 文件中必须包含私钥和终端实体证书。</p>
     *
     * @param location PEM 文件路径（支持 classpath:/、file:/ 等 Spring 资源协议，非空）
     * @param keypass  私钥密码，为 {@code null} 时表示私钥未加密
     * @return {@link KeyBundle}（非 {@code null}）
     * @throws IllegalArgumentException 若位置为空、资源不存在、PEM 内容无效或不含私钥
     */
    public static KeyBundle loadFromPem(String location, @Nullable String keypass) {
        Assert.hasText(location, "location must not be empty");

        var text = ResourceUtils.loadText(location);
        var pc = PemContent.of(text);
        Assert.notNull(pc, "failed to parse PEM content from: " + location);

        if (pc.getPrivateKey(keypass) == null) {
            throw new IllegalArgumentException("no private key found in PEM: " + location);
        }

        return new KeyBundleImpl(pc.getCertificates(), pc.getPrivateKey(keypass));
    }

    /**
     * 从 KeyStore 加载 {@link KeyBundle}。
     *
     * @param location  KeyStore 文件路径（支持 Spring 资源协议，非空）
     * @param type      KeyStore 类型（非 {@code null}）
     * @param storepass KeyStore 密码（非空）
     * @param alias     证书别名（非空）
     * @param keypass   私钥密码，为 {@code null} 时使用 {@code storepass}
     * @return {@link KeyBundle}（非 {@code null}）
     * @throws IllegalArgumentException 若参数无效、KeyStore 加载失败或别名不存在
     */
    public static KeyBundle loadFromStore(String location, KeyStoreType type, String storepass, String alias, @Nullable String keypass) {
        Assert.hasText(location, "location must not be empty");
        Assert.notNull(type, "type must not be null");
        Assert.hasText(storepass, "storepass must not be empty");
        Assert.hasText(alias, "alias must not be empty");

        var input = ResourceUtils.loadAsInputStream(location);
        var ks = KeyStoreUtils.loadKeyStore(input, type, storepass);

        if (keypass == null || keypass.isBlank()) {
            keypass = storepass;
        }

        var certChain = KeyStoreUtils.getCertificateChain(ks, alias);
        var privateKey = KeyStoreUtils.getPrivateKey(ks, alias, keypass);
        return new KeyBundleImpl(certChain, privateKey);
    }

}
